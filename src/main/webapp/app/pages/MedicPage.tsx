import React, { useCallback, useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import {
  Clinica,
  Locatie,
  Program,
  Programare,
  ProgramareStatus,
  getClinici,
  getLocatiiByClinica,
  getPrograms,
  getAppointmentsForMedicOnDate,
  getAppointmentsForMedicRange,
  cancelAppointment,
  updateAppointment,
  getMedici,
  type Medic,
  type ID,
} from 'app/shared/api/pacient-api';
import axios from 'axios';
import { useAppSelector } from 'app/config/store';
import ReportFormModal from './ReportFormModal';

function toDayRangeISO(dateStr: string) {
  const start = dayjs(dateStr).startOf('day');
  const end = start.add(1, 'day');
  return { startIso: start.toISOString(), endIso: end.toISOString() };
}

export default function MedicPage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = !!account?.authorities?.includes('ROLE_ADMIN');

  const [loading, setLoading] = useState(false);

  const [clinici, setClinici] = useState<Clinica[]>([]);
  const [locatii, setLocatii] = useState<Locatie[]>([]);
  const [medici, setMedici] = useState<Medic[]>([]); // <-- lista de medici pt admin
  const [programs, setPrograms] = useState<Program[]>([]);
  const [appointments, setAppointments] = useState<Programare[]>([]);

  const [clinicaId, setClinicaId] = useState<ID | undefined>(undefined);
  const [locatieId, setLocatieId] = useState<ID | undefined>(undefined);
  const [medicId, setMedicId] = useState<ID | undefined>(undefined);

  const [selectedDate, setSelectedDate] = useState<string>(dayjs().format('YYYY-MM-DD'));
  const [currentMonth, setCurrentMonth] = useState<dayjs.Dayjs>(dayjs().startOf('month'));
  const [monthlyAppointments, setMonthlyAppointments] = useState<Programare[]>([]);

  const [showPrescriptionModal, setShowPrescriptionModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState<Programare | null>(null);

  const hasFinalizata = useMemo(() => Object.prototype.hasOwnProperty.call(ProgramareStatus, 'FINALIZATA'), []);

  const handleOpenPrescription = (a: Programare) => {
    setSelectedAppointment(a);
    setShowPrescriptionModal(true);
  };

  // --- încărcări ---

  // clinici
  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        if (!isAdmin && account?.login) {
          const { data: medics } = await axios.get('/api/medics', { params: { size: 1000 } });
          const me = medics.find((m: any) => m.user?.login === account.login);
          if (me) {
            setMedicId(me.id);
            const { data: myClinics } = await axios.get('/api/clinicas', { params: { medicUserLogin: account.login, size: 1000 } });
            setClinici(myClinics || []);
          }
        } else {
          const c = await getClinici();
          setClinici(c);
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [isAdmin, account]);

  // locații după clinică
  useEffect(() => {
    (async () => {
      if (!clinicaId) {
        setLocatii([]);
        setLocatieId(undefined);
        if (isAdmin) {
          setMedici([]); // reset și lista de medici
          setMedicId(undefined);
        }
        return;
      }
      setLoading(true);
      try {
        const l = await getLocatiiByClinica(clinicaId);
        setLocatii(l);

        // Auto-select location for medics to hide the dropdown entirely
        if (!isAdmin && l.length > 0) {
          setLocatieId(l[0].id);
        }

        // pentru admin: după alegerea clinicii, încarcă medicii din clinică (sau toți dacă API-ul tău ignoră filtru)
        if (isAdmin) {
          const m = await getMedici({ clinicaId });
          setMedici(m);
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [clinicaId, isAdmin]);

  // program medic+locație
  useEffect(() => {
    (async () => {
      if (!medicId || !locatieId) {
        setPrograms([]);
        return;
      }
      setLoading(true);
      try {
        const p = await getPrograms(medicId, locatieId);
        setPrograms(p);
      } finally {
        setLoading(false);
      }
    })();
  }, [medicId, locatieId]);

  // programări pe zi (medic+locație+dată)
  const lastFetchParams = React.useRef<string>('');

  const loadAppointments = useCallback(async () => {
    if (!selectedDate || !medicId || !locatieId) {
      setAppointments([]);
      return;
    }

    const { startIso, endIso } = toDayRangeISO(selectedDate);
    const paramsKey = `${medicId}-${locatieId}-${startIso}-${endIso}`;

    if (loading || lastFetchParams.current === paramsKey) return;

    setLoading(true);
    lastFetchParams.current = paramsKey;
    try {
      const items = await getAppointmentsForMedicOnDate(medicId, locatieId, startIso, endIso);
      items.sort((a, b) => (a.dataProgramare < b.dataProgramare ? -1 : 1));
      setAppointments(items);
    } catch (err) {
      console.error('Error loading appointments:', err);
    } finally {
      setLoading(false);
    }
  }, [selectedDate, medicId, locatieId, loading]);

  useEffect(() => {
    loadAppointments();
  }, [loadAppointments]);

  // programări pe lună pentru calendar
  useEffect(() => {
    (async () => {
      if (!medicId) {
        setMonthlyAppointments([]);
        return;
      }
      const start = currentMonth.startOf('month').toISOString();
      const end = currentMonth.endOf('month').toISOString();
      try {
        const data = await getAppointmentsForMedicRange(start, end, isAdmin ? medicId : undefined);
        setMonthlyAppointments(data);
      } catch (err) {
        console.error('Error fetching monthly appts:', err);
      }
    })();
  }, [medicId, currentMonth, isAdmin]);

  // --- acțiuni ---

  async function markFinalizata(a: Programare) {
    if (!a.id) return;
    setLoading(true);
    try {
      if (hasFinalizata) {
        await updateAppointment(a.id, { status: ProgramareStatus.FINALIZATA as any });
      }
      await loadAppointments();
    } finally {
      setLoading(false);
    }
  }

  async function handleCancel(a: Programare) {
    if (!a.id) return;
    setLoading(true);
    try {
      await cancelAppointment(a.id);
      await loadAppointments();
    } finally {
      setLoading(false);
    }
  }

  // --- UI ---

  return (
    <div className="container py-4 fade-in-up">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="mb-0 fw-bold text-dark">Agenda medicului</h2>
        <div className="text-secondary small">
          Conectat ca: <span className="fw-bold text-primary">{account?.login}</span>
        </div>
      </div>

      {!isAuthenticated && <div className="alert alert-warning">Trebuie să fii autentificat pentru a accesa această pagină.</div>}

      {/* SECȚIUNE CALENDAR - vizibilă DOAR pentru MEDICI, nu pentru ADMIN */}
      {!isAdmin && (
        <div className="card glass-panel border-0 mb-4 overflow-hidden">
          <div className="card-header bg-transparent border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
            <h4 className="mb-0 fw-bold">{currentMonth.format('MMMM YYYY')}</h4>
            <div className="btn-group shadow-sm rounded-pill bg-white p-1">
              <button
                className="btn btn-sm btn-light rounded-pill border-0"
                onClick={() => setCurrentMonth(prev => prev.subtract(1, 'month'))}
              >
                ❮
              </button>
              <button
                className="btn btn-sm btn-white text-primary fw-bold border-0 px-3"
                onClick={() => setCurrentMonth(dayjs().startOf('month'))}
              >
                Azi
              </button>
              <button className="btn btn-sm btn-light rounded-pill border-0" onClick={() => setCurrentMonth(prev => prev.add(1, 'month'))}>
                ❯
              </button>
            </div>
          </div>
          <div className="card-body p-0">
            <div className="calendar-grid">
              {['Lun', 'Mar', 'Mie', 'Joi', 'Vin', 'Sâm', 'Dum'].map(d => (
                <div
                  key={d}
                  className="calendar-day-header py-3 text-center text-muted small fw-bold text-uppercase border-bottom border-light"
                >
                  {d}
                </div>
              ))}
              {(() => {
                const days = [];
                const startOfMonth = currentMonth.startOf('month');
                const endOfMonth = currentMonth.endOf('month');
                const startDay = (startOfMonth.day() + 6) % 7; // Ajustăm pt Luni start

                // Zile goale înainte
                for (let i = 0; i < startDay; i++) {
                  days.push(
                    <div key={`prev-${i}`} className="calendar-cell bg-light bg-opacity-10 border-end border-bottom border-light" />,
                  );
                }

                // Zilele lunii
                for (let d = 1; d <= endOfMonth.date(); d++) {
                  const date = startOfMonth.date(d);
                  const dateStr = date.format('YYYY-MM-DD');
                  const isSelected = selectedDate === dateStr;
                  const isToday = dayjs().format('YYYY-MM-DD') === dateStr;

                  const dayAppts = monthlyAppointments.filter(a => dayjs(a.dataProgramare).format('YYYY-MM-DD') === dateStr);

                  days.push(
                    <div
                      key={dateStr}
                      className={`calendar-cell border-end border-bottom border-light p-2 position-relative cursor-pointer transition-all ${isSelected ? 'bg-primary bg-opacity-10 shadow-inset' : 'hover-bg-light'}`}
                      onClick={() => setSelectedDate(dateStr)}
                      style={{ minHeight: '100px', cursor: 'pointer' }}
                    >
                      <div className="d-flex justify-content-between align-items-start mb-1">
                        <span className={`small fw-bold px-2 py-1 rounded-pill ${isToday ? 'bg-primary text-white' : 'text-secondary'}`}>
                          {d}
                        </span>
                        {dayAppts.length > 0 && (
                          <span className="badge rounded-pill bg-soft-primary small" style={{ fontSize: '0.65rem' }}>
                            {dayAppts.length}
                          </span>
                        )}
                      </div>
                      <div className="calendar-events-container overflow-hidden">
                        {dayAppts.slice(0, 2).map((a, idx) => (
                          <div
                            key={a.id || idx}
                            className="calendar-event-pill bg-soft-success mb-1 truncate small px-2 rounded-pill"
                            style={{ fontSize: '0.7rem', padding: '2px 0' }}
                          >
                            {dayjs(a.dataProgramare).format('HH:mm')} {a.pacient?.user?.lastName || 'Pacient'}
                          </div>
                        ))}
                        {dayAppts.length > 2 && (
                          <div className="text-muted small ps-2" style={{ fontSize: '0.6rem' }}>
                            + încă {dayAppts.length - 2}
                          </div>
                        )}
                      </div>
                    </div>,
                  );
                }
                return days;
              })()}
            </div>
          </div>
        </div>
      )}

      <div className="card mb-4 glass-panel hover-lift border-0">
        <div className="card-body">
          <div className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Clinică</label>
              <select
                className="form-select"
                value={clinicaId ?? ''}
                onChange={e => {
                  const val = e.target.value ? Number(e.target.value) : undefined;
                  setClinicaId(val);
                  // reset dependent
                  setLocatieId(undefined);
                  if (isAdmin) {
                    setMedicId(undefined);
                  }
                  setPrograms([]);
                  setAppointments([]);
                }}
              >
                <option value="">— alege clinica —</option>
                {clinici.map(c => (
                  <option key={c.id} value={c.id}>
                    {c.nume}
                  </option>
                ))}
              </select>
            </div>

            {isAdmin && (
              <div className="col-md-4">
                <label className="form-label">Locație</label>
                <select
                  className="form-select"
                  value={locatieId ?? ''}
                  onChange={e => {
                    const val = e.target.value ? Number(e.target.value) : undefined;
                    setLocatieId(val);
                    setPrograms([]);
                    setAppointments([]);
                  }}
                  disabled={!clinicaId}
                >
                  <option value="">— alege locația —</option>
                  {locatii.map(l => (
                    <option key={l.id} value={l.id}>
                      {l.oras ? `${l.oras} — ` : ''}
                      {l.adresa ?? `Locația #${l.id}`}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Selector de MEDIC numai pentru ADMIN (pentru medici reali îl poți ascunde și seta automat medicId-ul curent) */}
            {isAdmin && (
              <div className="col-md-4">
                <label className="form-label">Medic</label>
                <select
                  className="form-select"
                  value={medicId ?? ''}
                  onChange={e => {
                    const val = e.target.value ? Number(e.target.value) : undefined;
                    setMedicId(val);
                    setPrograms([]);
                    setAppointments([]);
                  }}
                  disabled={!clinicaId}
                >
                  <option value="">— alege medicul —</option>
                  {medici.map(m => {
                    const numeMedic = m.user ? `Dr. ${m.user.lastName} ${m.user.firstName}` : `Medicul #${m.id}`;
                    return (
                      <option key={m.id} value={m.id}>
                        {m.gradProfesional ? `${m.gradProfesional} ` : ''}
                        {numeMedic}
                        {m.disponibil === false ? ' (indisponibil)' : ''}
                      </option>
                    );
                  })}
                </select>
              </div>
            )}

            <div className="col-md-4">
              <label className="form-label">Data</label>
              <input
                type="date"
                className="form-control"
                value={selectedDate}
                onChange={e => setSelectedDate(e.target.value)}
                disabled={!locatieId || (!medicId && isAdmin)}
                min={dayjs().format('YYYY-MM-DD')}
              />
            </div>
          </div>
        </div>
      </div>

      {programs.length > 0 && (
        <div className="card mb-4 glass-panel hover-lift border-0">
          <div className="card-header">Programul zilei</div>
          <div className="card-body">
            <ul className="mb-0">
              {programs.map(p => (
                <li key={p.id ?? `${p.ziuaSaptamanii}-${p.oraStart}-${p.oraFinal}`}>
                  {p.ziuaSaptamanii}: {dayjs(p.oraStart).format('HH:mm')} – {dayjs(p.oraFinal).format('HH:mm')}
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}

      <h3 className="mb-3">Programări ({dayjs(selectedDate).format('YYYY-MM-DD')})</h3>
      <div className="card glass-panel hover-lift border-0">
        <div className="card-body">
          {!locatieId || (!medicId && isAdmin) ? (
            <div className="text-muted">Selectează clinica, locația {isAdmin ? 'și medicul' : ''} pentru a vedea agenda.</div>
          ) : appointments.length === 0 ? (
            <div className="text-muted">Nu există programări pentru ziua selectată.</div>
          ) : (
            <div className="table-responsive">
              <table className="table table-borderless table-hover align-middle mt-2">
                <thead>
                  <tr>
                    <th>Ora</th>
                    <th>Pacient</th>
                    <th>Observații</th>
                    <th>Status</th>
                    <th style={{ width: 260 }}>Acțiuni</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map(a => {
                    const d = dayjs(a.dataProgramare);
                    return (
                      <tr key={a.id}>
                        <td>{d.format('HH:mm')}</td>
                        <td>
                          {a.pacient?.user?.lastName || a.pacient?.user?.firstName
                            ? `${a.pacient.user.lastName || ''} ${a.pacient.user.firstName || ''}`.trim()
                            : a.pacient?.user?.login
                              ? `Pacient: ${a.pacient.user.login}`
                              : a.pacient?.cnp
                                ? `Pacient (CNP: ${a.pacient.cnp})`
                                : a.pacientId
                                  ? `Pacient #${a.pacientId}`
                                  : '-'}
                        </td>
                        <td className="text-truncate" style={{ maxWidth: 280 }} title={a.observatii || ''}>
                          {a.observatii || <span className="text-muted">—</span>}
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
                          <div className="d-flex flex-wrap gap-2">
                            {hasFinalizata && a.status === ProgramareStatus.ACTIVA && (
                              <button type="button" className="btn btn-outline-success btn-sm" onClick={() => markFinalizata(a)}>
                                Marchează finalizată
                              </button>
                            )}
                            {a.status === ProgramareStatus.ACTIVA && (
                              <button type="button" className="btn btn-outline-danger btn-sm" onClick={() => handleCancel(a)}>
                                Anulează
                              </button>
                            )}
                            <button type="button" className="btn btn-outline-primary btn-sm" onClick={() => handleOpenPrescription(a)}>
                              {a.fisaMedicala ? 'Editează Rețetă' : 'Scrie Rețetă'}
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

      <ReportFormModal
        isOpen={showPrescriptionModal}
        toggle={() => setShowPrescriptionModal(false)}
        programare={selectedAppointment}
        onSuccess={loadAppointments}
      />

      {loading && (
        <div className="position-fixed bottom-0 end-0 p-3" style={{ zIndex: 1080 }}>
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      )}

      {/* Styles pentru Calendar */}
      <style>{`
        .calendar-grid {
          display: grid;
          grid-template-columns: repeat(7, 1fr);
          background: rgba(255, 255, 255, 0.4);
        }
        .calendar-cell {
          transition: all 0.2s ease;
        }
        .hover-bg-light:hover {
          background-color: rgba(255, 255, 255, 0.8) !important;
        }
        .calendar-event-pill {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          max-width: 100%;
          border-left: 3px solid var(--bs-success);
        }
        .shadow-inset {
          box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);
        }
      `}</style>
    </div>
  );
}
