import React, { useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import {
  Clinica,
  Locatie,
  Specializare,
  Medic,
  Program,
  Programare,
  ProgramareStatus,
  getLocatii,
  getClinicsByLocatie,
  getSpecializari,
  getMedici,
  getPrograms,
  getAppointmentsForMedicOnDate,
  getCurrentPacientByUserId,
  getMyAppointments,
  createAppointment,
  updateAppointment,
  cancelAppointment,
  addToWaitlist,
  type ID,
} from 'app/shared/api/pacient-api';
import { useAppSelector } from 'app/config/store';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFileMedical, faStethoscope, faPills, faLightbulb, faPrint, faUserMd, faHospital } from '@fortawesome/free-solid-svg-icons';
// import MedicalChatbot from 'app/shared/chatbot/MedicalChatbot';

const SLOT_MINUTES = 30;

function toDayRangeISO(dateStr: string) {
  const start = dayjs(dateStr).startOf('day');
  const end = start.add(1, 'day');
  return { startIso: start.toISOString(), endIso: end.toISOString(), start, end };
}

function buildSlots(programs: Program[], selectedDate: string, taken: Programare[]) {
  if (!selectedDate) return [];
  const date = dayjs(selectedDate);

  // indexul zilei: 0 = Duminică ... 6 = Sâmbătă
  const dowIndex = date.day();
  const EN = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  const RO = ['DUMINICA', 'LUNI', 'MARTI', 'MIERCURI', 'JOI', 'VINERI', 'SAMBATA'];
  const keysForDay = new Set([EN[dowIndex], RO[dowIndex]]);

  // normalizăm stringul zilei din program (upper + fără diacritice + trim)
  const normalize = (s: string) => {
    const up = (s || '').trim().toUpperCase();
    const map: Record<string, string> = {
      Ă: 'A',
      Â: 'A',
      Î: 'I',
      Ș: 'S',
      Ş: 'S',
      Ț: 'T',
      Ţ: 'T',
    };
    return up.replace(/[ĂÂÎȘŞȚŢ]/g, ch => map[ch] || ch);
  };

  // colectăm intervalele orare pentru ziua selectată
  const intervals = programs
    .filter(p => {
      const key = normalize(p.ziuaSaptamanii || '');
      return Array.from(keysForDay).some(k => key.includes(k));
    })
    .map(p => {
      const startT = dayjs(p.oraStart);
      const endT = dayjs(p.oraFinal);
      const start = date.hour(startT.hour()).minute(startT.minute()).second(0).millisecond(0);
      const end = date.hour(endT.hour()).minute(endT.minute()).second(0).millisecond(0);
      return { start, end };
    });

  // sloturi deja ocupate (comparație la nivel de minut) - excludem cele anulate
  const takenSet = new Set(
    taken.filter(a => a.status !== ProgramareStatus.ANULATA).map(a => dayjs(a.dataProgramare).second(0).millisecond(0).toISOString()),
  );

  const now = dayjs();
  const slots: { iso: string; label: string; disabled: boolean }[] = [];

  intervals.forEach(({ start, end }) => {
    let cursor = start.clone();
    while (cursor.isBefore(end)) {
      const iso = cursor.toISOString();
      const label = cursor.format('HH:mm');
      const isPast = cursor.isBefore(now);
      const isTaken = takenSet.has(iso);
      slots.push({ iso, label, disabled: isPast || isTaken });
      cursor = cursor.add(SLOT_MINUTES, 'minute');
    }
  });

  // deduplicăm
  const unique = new Map<string, { iso: string; label: string; disabled: boolean }>();
  slots
    .sort((a, b) => (a.iso < b.iso ? -1 : 1))
    .forEach(s => {
      if (!unique.has(s.iso)) unique.set(s.iso, s);
      else if (s.disabled) unique.set(s.iso, s);
    });

  return Array.from(unique.values());
}

interface BookingFormProps {
  locatii: Locatie[];
  clinici: Clinica[];
  specializari: Specializare[];
  medici: Medic[];
  loading: boolean;
  isAuthenticated: boolean;
  pacientId: ID | undefined;
  locatieId: ID | undefined;
  clinicaId: ID | undefined;
  specializareId: ID | undefined;
  medicId: ID | undefined;
  selectedDate: string;
  selectedSlotIso: string;
  observatii: string;
  rescheduleId: ID | null;
  slots: { iso: string; label: string; disabled: boolean }[];
  setLocatieId: (id: ID | undefined) => void;
  setClinicaId: (id: ID | undefined) => void;
  setSpecializareId: (id: ID | undefined) => void;
  setMedicId: (id: ID | undefined) => void;
  setSelectedDate: (d: string) => void;
  setSelectedSlotIso: (s: string) => void;
  setObservatii: (o: string) => void;
  setRescheduleId: (id: ID | null) => void;
  handleCreateOrUpdate: () => void;
  handleJoinWaitlist: () => void;
}

const BookingForm: React.FC<BookingFormProps> = props => {
  const {
    locatii,
    clinici,
    specializari,
    medici,
    loading,
    isAuthenticated,
    pacientId,
    locatieId,
    clinicaId,
    specializareId,
    medicId,
    selectedDate,
    selectedSlotIso,
    observatii,
    rescheduleId,
    slots,
    setLocatieId,
    setClinicaId,
    setSpecializareId,
    setMedicId,
    setSelectedDate,
    setSelectedSlotIso,
    setObservatii,
    setRescheduleId,
    handleCreateOrUpdate,
    handleJoinWaitlist,
  } = props;

  return (
    <div className="card mb-5 glass-panel hover-lift border-0 p-2 p-md-4">
      <div className="card-body">
        <div className="row g-3 g-md-4">
          {/* Step 1: Locatie */}
          <div className="col-12 col-sm-6 col-md-3">
            <label className="form-label fw-semibold">1. Locație</label>
            <select
              className="form-select"
              value={locatieId ?? ''}
              onChange={e => {
                const val = e.target.value ? Number(e.target.value) : undefined;
                setLocatieId(val);
                setClinicaId(undefined);
                setSpecializareId(undefined);
                setMedicId(undefined);
                setSelectedSlotIso('');
              }}
            >
              <option value="">— alege orașul —</option>
              {locatii.map(l => (
                <option key={l.id} value={l.id}>
                  {l.oras ? l.oras : (l.adresa ?? `Locația #${l.id}`)}
                </option>
              ))}
            </select>
          </div>

          {/* Step 2: Clinica */}
          <div className="col-12 col-sm-6 col-md-3">
            <label className="form-label fw-semibold">2. Clinică</label>
            <select
              className="form-select"
              value={clinicaId ?? ''}
              onChange={e => {
                const val = e.target.value ? Number(e.target.value) : undefined;
                setClinicaId(val);
                setSpecializareId(undefined);
                setMedicId(undefined);
                setSelectedSlotIso('');
              }}
              disabled={!locatieId || clinici.length === 0}
            >
              <option value="">
                {!locatieId ? '← Alege locația' : clinici.length === 0 ? 'Nicio clinică în această locație' : '— alege clinica —'}
              </option>
              {clinici.map(c => (
                <option key={c.id} value={c.id}>
                  {c.nume}
                </option>
              ))}
            </select>
          </div>

          {/* Step 3: Sectie */}
          <div className="col-12 col-sm-6 col-md-3">
            <label className="form-label fw-semibold">3. Secție medicală</label>
            <select
              className="form-select"
              value={specializareId ?? ''}
              onChange={e => {
                const val = e.target.value ? Number(e.target.value) : undefined;
                setSpecializareId(val);
                setMedicId(undefined);
                setSelectedSlotIso('');
              }}
              disabled={!clinicaId}
            >
              <option value="">{!clinicaId ? '← Alege clinica' : '— alege secția —'}</option>
              {specializari.map(s => (
                <option key={s.id} value={s.id}>
                  {s.nume}
                </option>
              ))}
            </select>
          </div>

          {/* Step 4: Medic */}
          <div className="col-12 col-sm-6 col-md-3">
            <label className="form-label fw-semibold">4. Medic</label>
            <select
              className="form-select"
              value={medicId ?? ''}
              onChange={e => {
                const val = e.target.value ? Number(e.target.value) : undefined;
                setMedicId(val);
                setSelectedSlotIso('');
              }}
              disabled={!clinicaId || !specializareId || medici.length === 0}
            >
              <option value="">
                {!clinicaId
                  ? '← Alege clinica'
                  : !specializareId
                    ? '← Alege secția'
                    : medici.length === 0
                      ? 'Niciun medic disponibil'
                      : '— alege medicul —'}
              </option>
              {medici.map(m => (
                <option key={m.id} value={m.id}>
                  {m.user ? `Dr. ${m.user.lastName ?? ''} ${m.user.firstName ?? ''}`.trim() : `Medic #${m.id}`}
                  {m.gradProfesional ? ` — ${m.gradProfesional}` : ''}
                </option>
              ))}
            </select>
          </div>

          {/* Date + Observations */}
          <div className="col-md-6">
            <label className="form-label">Data</label>
            <input
              type="date"
              className="form-control"
              value={selectedDate}
              onChange={e => {
                setSelectedDate(e.target.value);
                setSelectedSlotIso('');
              }}
              disabled={!medicId}
              min={dayjs().format('YYYY-MM-DD')}
            />
          </div>

          <div className="col-md-6">
            <label className="form-label">
              Observații (opțional)
              {rescheduleId && <small className="text-muted ms-2">(Nu pot fi modificate la reprogramare)</small>}
            </label>
            <input
              type="text"
              className="form-control"
              value={observatii}
              onChange={e => setObservatii(e.target.value)}
              placeholder="Simptome, preferințe, etc."
              disabled={!!rescheduleId}
            />
          </div>
        </div>

        <div className="mt-4">
          <label className="form-label d-block">Intervale disponibile</label>
          {!medicId || !selectedDate ? (
            <div className="text-muted">Selectează medicul și data pentru a vedea intervalele.</div>
          ) : slots.length === 0 ? (
            <div className="text-danger">Nu există intervale disponibile pentru ziua selectată.</div>
          ) : (
            <div className="d-flex flex-wrap gap-2">
              {slots.map(s => (
                <button
                  key={s.iso}
                  type="button"
                  className={`btn rounded-pill px-4 py-2 ${selectedSlotIso === s.iso ? 'btn-primary' : 'btn-outline-primary'}`}
                  style={{ minWidth: '85px' }}
                  disabled={s.disabled}
                  onClick={() => setSelectedSlotIso(s.iso)}
                >
                  {s.label}
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="mt-4 d-flex flex-column flex-md-row gap-3">
          <button
            className="btn btn-success rounded-pill px-5 py-2 fw-bold"
            disabled={loading || !isAuthenticated || !pacientId || !medicId || !locatieId || !selectedSlotIso}
            onClick={handleCreateOrUpdate}
          >
            {rescheduleId ? 'Reprogramează' : 'Programează'}
          </button>
          {rescheduleId && (
            <button
              className="btn btn-link ms-2"
              onClick={() => {
                setRescheduleId(null);
                setSelectedSlotIso('');
              }}
            >
              Renunță la reprogramare
            </button>
          )}
          {!rescheduleId && medicId && selectedDate && slots.every(s => s.disabled) && (
            <button
              className="btn btn-outline-info rounded-pill ms-3 px-4 fw-bold shadow-sm"
              onClick={handleJoinWaitlist}
              disabled={loading}
            >
              <FontAwesomeIcon icon="clock" className="me-2" />
              Pune-mă pe lista de așteptare
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

interface AppointmentTableProps {
  appointments: Programare[];
  rescheduleId: ID | null;
  startReschedule: (a: Programare) => void;
  handleCancel: (a: Programare) => void;
  onViewPrescription: (a: Programare) => void;
}

const AppointmentTable: React.FC<AppointmentTableProps> = ({
  appointments,
  rescheduleId,
  startReschedule,
  handleCancel,
  onViewPrescription,
}) => {
  if (appointments.length === 0) {
    return (
      <div className="card glass-panel border-0 p-4 text-center mt-4">
        <div className="card-body text-muted">
          <span className="display-6 d-block mb-3">📅</span>
          Nu ai nicio programare activă momentan.
        </div>
      </div>
    );
  }

  return (
    <div className="card glass-panel hover-lift border-0 p-3 mt-4">
      <div className="card-body">
        <div className="table-responsive">
          <table className="table table-borderless table-hover align-middle mt-2">
            <thead>
              <tr>
                <th>Data</th>
                <th>Ora</th>
                <th>Medic</th>
                <th>Locație</th>
                <th>Status</th>
                <th style={{ width: 220 }}>Acțiuni</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map(a => {
                const d = dayjs(new Date(a.dataProgramare));
                const past = d.isBefore(dayjs());
                return (
                  <tr key={a.id}>
                    <td>{d.format('YYYY-MM-DD')}</td>
                    <td>{d.format('HH:mm')}</td>
                    <td>
                      {a.medic?.user
                        ? `Dr. ${a.medic.user.lastName ?? ''} ${a.medic.user.firstName ?? ''}`.trim()
                        : a.medicId
                          ? `Medic #${a.medicId}`
                          : '-'}
                    </td>
                    <td>
                      {a.clinica ? (
                        <>
                          <div className="fw-bold">{a.clinica.nume}</div>
                          {a.clinica.locatie && (
                            <small className="text-muted">
                              {a.clinica.locatie.oras ?? ''}
                              {a.clinica.locatie.oras && a.clinica.locatie.adresa ? ' — ' : ''}
                              {a.clinica.locatie.adresa ?? ''}
                            </small>
                          )}
                        </>
                      ) : a.clinicaId ? (
                        `Clinic #${a.clinicaId}`
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      <span
                        className={
                          a.status === ProgramareStatus.ACTIVA
                            ? 'badge rounded-pill bg-soft-success px-3 py-2'
                            : a.status === ProgramareStatus.ANULATA
                              ? 'badge rounded-pill bg-soft-danger px-3 py-2'
                              : 'badge rounded-pill bg-soft-primary px-3 py-2'
                        }
                      >
                        {a.status}
                      </span>
                    </td>
                    <td>
                      <div className="d-flex gap-2">
                        <button
                          className={`btn btn-sm rounded-pill fw-semibold ${rescheduleId === a.id ? 'btn-primary' : 'btn-outline-primary'}`}
                          disabled={a.status !== ProgramareStatus.ACTIVA || past}
                          onClick={() => startReschedule(a)}
                        >
                          {rescheduleId === a.id ? 'În curs...' : 'Reprogramează'}
                        </button>
                        <button
                          className="btn btn-sm btn-outline-danger rounded-pill fw-semibold border-0 bg-soft-danger shadow-none"
                          disabled={a.status !== ProgramareStatus.ACTIVA || past}
                          onClick={() => handleCancel(a)}
                        >
                          Anulează
                        </button>
                        {a.fisaMedicala && (
                          <button
                            className="btn btn-sm btn-primary rounded-pill fw-semibold d-flex align-items-center shadow-sm"
                            onClick={() => onViewPrescription(a)}
                          >
                            Rețetă
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

interface PrescriptionModalProps {
  isOpen: boolean;
  toggle: () => void;
  appointment: Programare | null;
}

const PrescriptionModal: React.FC<PrescriptionModalProps> = ({ isOpen, toggle, appointment }) => {
  return (
    <Modal isOpen={isOpen} toggle={toggle} size="lg" centered className="glass-modal custom-prescription-modal">
      <ModalHeader toggle={toggle} className="border-0 pb-0">
        <div className="d-flex align-items-center">
          <div className="bg-soft-primary p-3 rounded-4 me-3 text-primary shadow-sm">
            <FontAwesomeIcon icon={faFileMedical} size="lg" />
          </div>
          <div>
            <h4 className="m-0 text-dark fw-bold">Fișă Medicală / Rețetă</h4>
            <div className="d-flex align-items-center mt-1">
              <span className="badge bg-light text-secondary border rounded-pill small-text px-2">
                Emisă la data de {dayjs(new Date(appointment?.dataProgramare ?? '')).format('DD.MM.YYYY')}
              </span>
            </div>
          </div>
        </div>
      </ModalHeader>
      <ModalBody className="py-4 px-4">
        <div className="row g-4">
          <div className="col-md-6">
            <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs h-100 transition-hover">
              <div className="d-flex align-items-center mb-2">
                <FontAwesomeIcon icon={faUserMd} className="text-primary opacity-50 me-2" />
                <label className="small text-uppercase text-muted fw-bold mb-0">Medic Curant</label>
              </div>
              <div className="h5 mb-0 fw-bold text-dark">
                {appointment?.medic?.user ? `Dr. ${appointment.medic.user.lastName} ${appointment.medic.user.firstName}` : '-'}
              </div>
              <div className="text-primary small fw-semibold mt-1">{appointment?.medic?.gradProfesional}</div>
            </div>
          </div>
          <div className="col-md-6">
            <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs h-100 transition-hover">
              <div className="d-flex align-items-center mb-2">
                <FontAwesomeIcon icon={faHospital} className="text-primary opacity-50 me-2" />
                <label className="small text-uppercase text-muted fw-bold mb-0">Locație</label>
              </div>
              <div className="h5 mb-0 fw-bold text-dark">{appointment?.clinica?.nume}</div>
              <div className="text-muted small mt-1">
                {appointment?.clinica?.locatie?.adresa}, {appointment?.clinica?.locatie?.oras}
              </div>
            </div>
          </div>

          <div className="col-12 mt-4">
            <div className="mb-4">
              <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                <div
                  className="bg-soft-primary p-2 rounded-3 me-2 text-primary d-flex align-items-center justify-content-center"
                  style={{ width: '32px', height: '32px' }}
                >
                  <FontAwesomeIcon icon={faStethoscope} />
                </div>
                Diagnostic
              </h6>
              <div className="p-4 bg-white rounded-4 border border-light-subtle shadow-xs min-h-50 border-start border-4 border-start-primary">
                {appointment?.fisaMedicala?.diagnostic || 'Nespecificat'}
              </div>
            </div>

            <div className="mb-4">
              <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                <div
                  className="bg-soft-success p-2 rounded-3 me-2 text-success d-flex align-items-center justify-content-center"
                  style={{ width: '32px', height: '32px' }}
                >
                  <FontAwesomeIcon icon={faPills} />
                </div>
                Tratament și Rețetă
              </h6>
              <div className="p-4 bg-light bg-opacity-50 rounded-4 border border-light-subtle text-dark min-h-100 whitespace-pre-wrap border-start border-4 border-start-success shadow-xs">
                {appointment?.fisaMedicala?.tratament || 'Nespecificat'}
              </div>
            </div>

            <div className="mb-0">
              <h6 className="fw-bold text-dark d-flex align-items-center mb-3">
                <div
                  className="bg-soft-warning p-2 rounded-3 me-2 text-warning d-flex align-items-center justify-content-center"
                  style={{ width: '32px', height: '32px' }}
                >
                  <FontAwesomeIcon icon={faLightbulb} />
                </div>
                Recomandări
              </h6>
              <div className="p-4 bg-light bg-opacity-50 rounded-4 border border-light-subtle text-dark min-h-50 border-start border-4 border-start-warning shadow-xs">
                {appointment?.fisaMedicala?.recomandari || 'Nicio recomandare suplimentară.'}
              </div>
            </div>
          </div>
        </div>
      </ModalBody>
      <ModalFooter className="border-0 bg-light bg-opacity-50 py-3">
        <Button color="secondary" outline pill onClick={toggle} className="rounded-pill px-4 border-0">
          Închide
        </Button>
        <Button color="primary" pill onClick={() => window.print()} className="rounded-pill px-4 shadow-sm fw-bold">
          <FontAwesomeIcon icon={faPrint} className="me-2" />
          Printează Rețeta
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default function PacientPage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const [loading, setLoading] = useState(false);
  const [locatii, setLocatii] = useState<Locatie[]>([]);
  const [clinici, setClinici] = useState<Clinica[]>([]);
  const [specializari, setSpecializari] = useState<Specializare[]>([]);
  const [medici, setMedici] = useState<Medic[]>([]);
  const [programs, setPrograms] = useState<Program[]>([]);
  const [appointments, setAppointments] = useState<Programare[]>([]);

  const [pacientId, setPacientId] = useState<ID | undefined>(undefined);

  const [locatieId, setLocatieId] = useState<ID | undefined>(undefined);
  const [clinicaId, setClinicaId] = useState<ID | undefined>(undefined);
  const [specializareId, setSpecializareId] = useState<ID | undefined>(undefined);
  const [medicId, setMedicId] = useState<ID | undefined>(undefined);

  const [selectedDate, setSelectedDate] = useState<string>(dayjs().format('YYYY-MM-DD'));
  const [takenForDate, setTakenForDate] = useState<Programare[]>([]);
  const [selectedSlotIso, setSelectedSlotIso] = useState<string>('');
  const [observatii, setObservatii] = useState<string>('');

  const [rescheduleId, setRescheduleId] = useState<ID | null>(null);

  const [showViewModal, setShowViewModal] = useState(false);
  const [selectedAppointmentForView, setSelectedAppointmentForView] = useState<Programare | null>(null);

  // Load all locations + specializations on mount
  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const [l, s] = await Promise.all([getLocatii(), getSpecializari()]);
        setLocatii(l);
        setSpecializari(s);

        if (isAuthenticated && account?.id) {
          const pacient = await getCurrentPacientByUserId(account.id);
          if (pacient?.id) {
            setPacientId(pacient.id);
            const my = await getMyAppointments(pacient.id);
            setAppointments(my);
          }
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [isAuthenticated, account?.id]);

  // Locatie selected → load clinics for that location
  useEffect(() => {
    (async () => {
      if (!locatieId) {
        setClinici([]);
        setClinicaId(undefined);
        setMedici([]);
        setMedicId(undefined);
        return;
      }
      setLoading(true);
      try {
        const c = await getClinicsByLocatie(locatieId);
        setClinici(c);
      } finally {
        setLoading(false);
      }
    })();
  }, [locatieId]);

  // Clinica + Sectie both selected → load doctors
  useEffect(() => {
    (async () => {
      if (!clinicaId || !specializareId) {
        setMedici([]);
        setMedicId(undefined);
        return;
      }
      setLoading(true);
      try {
        const m = await getMedici({ clinicaId, specializareId });
        setMedici(m);
      } finally {
        setLoading(false);
      }
    })();
  }, [clinicaId, specializareId]);

  const clinicLocatieId = locatieId;

  useEffect(() => {
    (async () => {
      if (!medicId || !clinicLocatieId) {
        setPrograms([]);
        return;
      }
      setLoading(true);
      try {
        const p = await getPrograms(medicId, clinicLocatieId);
        setPrograms(p);
      } finally {
        setLoading(false);
      }
    })();
  }, [medicId, clinicLocatieId]);

  useEffect(() => {
    (async () => {
      if (!selectedDate || !medicId || !clinicLocatieId) {
        setTakenForDate([]);
        return;
      }
      const { startIso, endIso } = toDayRangeISO(selectedDate);
      setLoading(true);
      try {
        const taken = await getAppointmentsForMedicOnDate(medicId, clinicLocatieId, startIso, endIso);
        setTakenForDate(taken);
      } finally {
        setLoading(false);
      }
    })();
  }, [selectedDate, medicId, clinicLocatieId]);

  const slots = useMemo(() => buildSlots(programs, selectedDate, takenForDate), [programs, selectedDate, takenForDate]);

  async function handleJoinWaitlist() {
    if (!pacientId || !medicId || !clinicaId || !clinicLocatieId || !selectedDate) {
      alert('Lipsesc date necesare pentru lista de așteptare!');
      return;
    }
    setLoading(true);
    try {
      await addToWaitlist({
        pacientId,
        medicId,
        clinicaId,
        locatieId: clinicLocatieId,
        dataPreferata: dayjs(selectedDate).toISOString(),
        status: 'WAITING',
      });
      alert('Ai fost adaugat cu succes pe lista de așteptare! Te vom notifica dacă se eliberează un loc.');
    } catch (err: any) {
      alert(`Eroare: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateOrUpdate() {
    if (!pacientId || !medicId || !clinicaId || !clinicLocatieId || !selectedSlotIso) {
      alert(`Lipsesc date necesare!`);
      return;
    }

    setLoading(true);
    try {
      if (rescheduleId) {
        await updateAppointment(rescheduleId, {
          dataProgramare: selectedSlotIso,
          medicId,
          clinicaId,
          status: ProgramareStatus.ACTIVA,
          observatii,
        });
        setRescheduleId(null);
        alert('Programarea a fost reprogamată cu succes!');
      } else {
        await createAppointment({ pacientId, medicId, clinicaId, dataProgramare: selectedSlotIso, observatii });
        alert('Programarea a fost creată cu succes!');
      }
      const my = await getMyAppointments(pacientId);
      setAppointments(my);

      // Re-împrospătăm intervalele ocupate pentru medicul și ziua selectată
      if (medicId && clinicLocatieId && selectedDate) {
        const { startIso, endIso } = toDayRangeISO(selectedDate);
        const taken = await getAppointmentsForMedicOnDate(medicId, clinicLocatieId, startIso, endIso);
        setTakenForDate(taken);
      }

      setSelectedSlotIso('');
      setObservatii('');
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Eroare necunoscută';
      alert(`Eroare la salvare: ${msg}`);
    } finally {
      setLoading(false);
    }
  }

  async function handleCancel(a: Programare) {
    if (!a.id) return;
    setLoading(true);
    try {
      await cancelAppointment(a.id);
      if (pacientId) {
        const my = await getMyAppointments(pacientId);
        setAppointments(my);
      }
      if (a.medicId && a.locatieId && selectedDate) {
        const { startIso, endIso } = toDayRangeISO(selectedDate);
        const taken = await getAppointmentsForMedicOnDate(a.medicId, a.locatieId, startIso, endIso);
        setTakenForDate(taken);
      }
    } finally {
      setLoading(false);
    }
  }

  function startReschedule(a: Programare) {
    setRescheduleId(a.id);
    if (a.medicId) setMedicId(a.medicId);

    const locId = a.locatieId || a.clinica?.locatie?.id;
    if (locId) setLocatieId(locId);

    if (a.clinicaId) setClinicaId(a.clinicaId);

    setSelectedDate(dayjs(a.dataProgramare).format('YYYY-MM-DD'));
    setSelectedSlotIso(a.dataProgramare);
    setObservatii(a.observatii || '');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  return (
    <div className="container py-5 fade-in-up" style={{ maxWidth: '1100px' }}>
      <div className="d-flex align-items-center mb-5">
        <div>
          <h2 className="mb-0 fw-bold text-dark">Gestionează Programările</h2>
          <p className="text-secondary mb-0">Programează o nouă consultație sau administrează-le pe cele existente.</p>
        </div>
      </div>

      {!isAuthenticated && <div className="alert alert-warning">Trebuie să fii autentificat pentru a face o programare.</div>}
      {isAuthenticated && !pacientId && <div className="alert alert-info">Îți căutăm profilul de pacient...</div>}

      <BookingForm
        locatii={locatii}
        clinici={clinici}
        specializari={specializari}
        medici={medici}
        loading={loading}
        isAuthenticated={isAuthenticated}
        pacientId={pacientId}
        locatieId={locatieId}
        clinicaId={clinicaId}
        specializareId={specializareId}
        medicId={medicId}
        selectedDate={selectedDate}
        selectedSlotIso={selectedSlotIso}
        observatii={observatii}
        rescheduleId={rescheduleId}
        slots={slots}
        setLocatieId={setLocatieId}
        setClinicaId={setClinicaId}
        setSpecializareId={setSpecializareId}
        setMedicId={setMedicId}
        setSelectedDate={setSelectedDate}
        setSelectedSlotIso={setSelectedSlotIso}
        setObservatii={setObservatii}
        setRescheduleId={setRescheduleId}
        handleCreateOrUpdate={handleCreateOrUpdate}
        handleJoinWaitlist={handleJoinWaitlist}
      />

      <h3 className="mb-3">Programările mele</h3>
      <div className="table-wrapper">
        <AppointmentTable
          appointments={appointments}
          rescheduleId={rescheduleId}
          startReschedule={startReschedule}
          handleCancel={handleCancel}
          onViewPrescription={a => {
            setSelectedAppointmentForView(a);
            setShowViewModal(true);
          }}
        />
      </div>

      {loading && (
        <div className="position-fixed bottom-0 end-0 p-3" style={{ zIndex: 1080 }}>
          <div className="toast show align-items-center text-bg-dark border-0">
            <div className="d-flex">
              <div className="toast-body">Se încarcă...</div>
            </div>
          </div>
        </div>
      )}

      <PrescriptionModal isOpen={showViewModal} toggle={() => setShowViewModal(false)} appointment={selectedAppointmentForView} />

      <style>{`
        .glass-panel {
          backdrop-filter: blur(10px);
          background: rgba(255, 255, 255, 0.7);
        }
        .hover-lift:hover {
          transform: translateY(-5px);
          transition: transform 0.2s;
        }
        @media (max-width: 768px) {
          .container {
            padding-left: 15px;
            padding-right: 15px;
          }
          h2 {
            font-size: 1.5rem !important;
          }
          .card-body {
            padding: 1.25rem !important;
          }
          .btn-sm {
            padding: 0.5rem 1rem !important;
          }
          .table td, .table th {
            font-size: 0.85rem !important;
            padding: 12px 8px !important;
          }
        }
      `}</style>
    </div>
  );
}
