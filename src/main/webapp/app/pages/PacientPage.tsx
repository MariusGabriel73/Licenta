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
  type ID,
} from 'app/shared/api/pacient-api';
import { useAppSelector } from 'app/config/store';

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

  // sloturi deja ocupate (comparație la nivel de minut)
  const takenSet = new Set(taken.map(a => dayjs(a.dataProgramare).second(0).millisecond(0).toISOString()));

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

  // Step 1: Select location
  // Step 2: Select clinic (filtered by Step 1 location)
  // Therefore, the location of the selected clinic is ALWAYS the value of locatieId from Step 1.
  const clinicLocatieId = locatieId;

  useEffect(() => {
    // când am medic + locația clinicii -> aduc programul
    (async () => {
      if (!medicId || !clinicLocatieId) {
        setPrograms([]);
        return;
      }
      console.warn('DEBUG: Fetching programs for medicId:', medicId, 'and clinicLocatieId:', clinicLocatieId);
      setLoading(true);
      try {
        const p = await getPrograms(medicId, clinicLocatieId);
        console.warn('DEBUG: Fetched programs count:', p.length);
        setPrograms(p);
      } finally {
        setLoading(false);
      }
    })();
  }, [medicId, clinicLocatieId]);

  useEffect(() => {
    // când se schimbă data/medic/locația clinicii -> aduc programările deja făcute în ziua respectivă
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

  async function handleCreateOrUpdate() {
    if (!pacientId || !medicId || !clinicLocatieId || !selectedSlotIso) return;

    setLoading(true);
    try {
      if (rescheduleId) {
        await updateAppointment(rescheduleId, {
          dataProgramare: selectedSlotIso,
          medicId,
          locatieId: clinicLocatieId,
          status: ProgramareStatus.ACTIVA,
          observatii,
        });
        setRescheduleId(null);
      } else {
        await createAppointment({
          pacientId,
          medicId,
          locatieId: clinicLocatieId,
          dataProgramare: selectedSlotIso,
          observatii,
        });
      }
      // refresh lista mea
      const my = await getMyAppointments(pacientId);
      setAppointments(my);
      // reset slot selectat
      // păstrăm selecțiile curente (clinică/locație etc.) pentru a putea continua rapid
      setSelectedSlotIso('');
      setObservatii('');
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
      // dacă anulez slot din ziua curentă, reîncarc taken
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
    // preselectez medic/locație/date în funcție de programarea selectată
    if (a.medicId) setMedicId(a.medicId);
    if (a.locatieId) setLocatieId(a.locatieId);
    const dateStr = dayjs(a.dataProgramare).format('YYYY-MM-DD');
    setSelectedDate(dateStr);
    setSelectedSlotIso(a.dataProgramare);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  return (
    <div className="container py-4 fade-in-up">
      <h2 className="mb-4">Programare consultație</h2>

      {!isAuthenticated && <div className="alert alert-warning">Trebuie să fii autentificat pentru a face o programare.</div>}

      {isAuthenticated && !pacientId && <div className="alert alert-info">Îți căutăm profilul de pacient...</div>}

      <div className="card mb-4 glass-panel hover-lift border-0">
        <div className="card-body">
          <div className="row g-3">
            {/* Step 1: Locatie */}
            <div className="col-md-3">
              <label className="form-label fw-semibold">1. Locație</label>
              <select
                className="form-select"
                value={locatieId ?? ''}
                onChange={e => {
                  const val = e.target.value ? Number(e.target.value) : undefined;
                  setLocatieId(val);
                  // reset all dependent
                  setClinicaId(undefined);
                  setClinici([]);
                  setSpecializareId(undefined);
                  setMedicId(undefined);
                  setMedici([]);
                  setPrograms([]);
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

            {/* Step 2: Clinica (filtered by locatie) */}
            <div className="col-md-3">
              <label className="form-label fw-semibold">2. Clinică</label>
              <select
                className="form-select"
                value={clinicaId ?? ''}
                onChange={e => {
                  const val = e.target.value ? Number(e.target.value) : undefined;
                  setClinicaId(val);
                  setSpecializareId(undefined);
                  setMedicId(undefined);
                  setMedici([]);
                  setPrograms([]);
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

            {/* Step 3: Sectie (all sections, filter doctors on select) */}
            <div className="col-md-3">
              <label className="form-label fw-semibold">3. Secție medicală</label>
              <select
                className="form-select"
                value={specializareId ?? ''}
                onChange={e => {
                  const val = e.target.value ? Number(e.target.value) : undefined;
                  setSpecializareId(val);
                  setMedicId(undefined);
                  setPrograms([]);
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
            <div className="col-md-3">
              <label className="form-label fw-semibold">4. Medic</label>
              <select
                className="form-select"
                value={medicId ?? ''}
                onChange={e => {
                  const val = e.target.value ? Number(e.target.value) : undefined;
                  setMedicId(val);
                  setPrograms([]);
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
              <label className="form-label">Observații (opțional)</label>
              <input
                type="text"
                className="form-control"
                value={observatii}
                onChange={e => setObservatii(e.target.value)}
                placeholder="Simptome, preferințe, etc."
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
                    className={`btn rounded-pill btn-sm ${selectedSlotIso === s.iso ? 'btn-primary' : 'btn-outline-primary'}`}
                    disabled={s.disabled}
                    onClick={() => setSelectedSlotIso(s.iso)}
                  >
                    {s.label}
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="mt-4">
            <button
              className="btn btn-success"
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
          </div>
        </div>
      </div>

      <h3 className="mb-3">Programările mele</h3>
      <div className="card glass-panel hover-lift border-0">
        <div className="card-body">
          {appointments.length === 0 ? (
            <div className="text-muted">Nu ai nicio programare.</div>
          ) : (
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
                    const d = dayjs(a.dataProgramare);
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
                          {a.locatie ? (a.locatie.oras ?? a.locatie.adresa ?? `#${a.locatieId}`) : a.locatieId ? `#${a.locatieId}` : '-'}
                        </td>
                        <td>
                          <span
                            className={
                              a.status === ProgramareStatus.ACTIVA
                                ? 'badge bg-success'
                                : a.status === ProgramareStatus.ANULATA
                                  ? 'badge bg-secondary'
                                  : 'badge bg-info'
                            }
                          >
                            {a.status}
                          </span>
                        </td>
                        <td>
                          <div className="d-flex gap-2">
                            <button
                              className="btn btn-outline-primary btn-sm"
                              disabled={a.status !== ProgramareStatus.ACTIVA || past}
                              onClick={() => startReschedule(a)}
                            >
                              Reprogramează
                            </button>
                            <button
                              className="btn btn-outline-danger btn-sm"
                              disabled={a.status !== ProgramareStatus.ACTIVA || past}
                              onClick={() => handleCancel(a)}
                            >
                              Anulează
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
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
    </div>
  );
}
