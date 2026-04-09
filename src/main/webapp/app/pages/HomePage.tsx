import React, { useEffect, useState } from 'react';
import { Navigate, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import {
  Programare,
  getCurrentPacientByUserId,
  getMyAppointments,
  getMyMedicationDoses,
  getMyWaitlist,
  confirmMedicationDose,
  claimWaitlistSpot,
  type ID,
} from 'app/shared/api/pacient-api';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Card, CardBody } from 'reactstrap';

export default function HomePage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const [loading, setLoading] = useState(false);
  const [pacientId, setPacientId] = useState<ID | undefined>(undefined);
  const [appointments, setAppointments] = useState<Programare[]>([]);
  const [currentMonth, setCurrentMonth] = useState<dayjs.Dayjs>(dayjs().startOf('month'));

  const [medicationDoses, setMedicationDoses] = useState<any[]>([]);
  const [waitlistEntries, setWaitlistEntries] = useState<any[]>([]);
  const [selectedDayProg, setSelectedDayProg] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      if (isAuthenticated && account?.id && account.authorities?.includes(AUTHORITIES.PACIENT)) {
        setLoading(true);
        try {
          const p = await getCurrentPacientByUserId(account.id);
          if (p?.id) {
            setPacientId(p.id);
            const [myAppts, myDoses, myWaitlist] = await Promise.all([
              getMyAppointments(p.id),
              getMyMedicationDoses(p.id),
              getMyWaitlist(p.id),
            ]);
            setAppointments(myAppts);
            setMedicationDoses(myDoses);
            setWaitlistEntries(myWaitlist);
          }
        } finally {
          setLoading(false);
        }
      }
    })();
  }, [isAuthenticated, account]);

  const handleConfirmDose = async (doseId: ID) => {
    try {
      await confirmMedicationDose(doseId);
      // Refresh doses
      if (pacientId) {
        const myDoses = await getMyMedicationDoses(pacientId);
        setMedicationDoses(myDoses);
      }
    } catch (err) {
      console.error('Error confirming dose', err);
    }
  };

  const handleClaimSpot = async (waitlistId: ID) => {
    if (!window.confirm('Ești sigur că vrei să ocupi acest loc? Această acțiune va crea o programare oficială.')) return;
    setLoading(true);
    try {
      await claimWaitlistSpot(waitlistId);
      // Refresh all data
      if (pacientId) {
        const [myAppts, myWaitlist] = await Promise.all([getMyAppointments(pacientId), getMyWaitlist(pacientId)]);
        setAppointments(myAppts);
        setWaitlistEntries(myWaitlist);
      }
    } catch (err) {
      console.error('Error claiming spot', err);
      alert('A apărut o eroare la ocuparea locului. Vă rugăm să încercați din nou.');
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  const todayDoses = medicationDoses.filter(d => dayjs(d.oraPlanificata).isSame(dayjs(), 'day'));
  const notifiedWaitlist = waitlistEntries.filter(w => w.status === 'NOTIFIED');

  if (account && account.authorities) {
    if (account.authorities.includes(AUTHORITIES.MEDIC)) {
      return <Navigate to="/medic" replace />;
    }
    if (account.authorities.includes(AUTHORITIES.ADMIN)) {
      return <Navigate to="/admin" replace />;
    }
  }

  const daysInMonth = () => {
    const days = [];
    const startOfMonth = currentMonth.startOf('month');
    const endOfMonth = currentMonth.endOf('month');
    const startDay = startOfMonth.day(); // 0 (Sun) to 6 (Sat)

    // Ajustam startDay pentru saptamana care incepe Luni (L=0, D=6)
    const adjustedStartDay = startDay === 0 ? 6 : startDay - 1;

    // Zilele din luna PRECEDENTA
    const prevMonth = startOfMonth.subtract(1, 'month');
    const prevMonthDays = prevMonth.daysInMonth();
    for (let i = adjustedStartDay - 1; i >= 0; i--) {
      const d = prevMonthDays - i;
      days.push(
        <div key={`prev-${d}`} className="calendar-cell bg-light bg-opacity-10 border-end border-bottom border-light p-1 p-md-2 opacity-50">
          <div className="text-secondary small">{d}</div>
        </div>,
      );
    }

    // Zilele lunii CURENTE
    for (let d = 1; d <= endOfMonth.date(); d++) {
      const date = startOfMonth.date(d);
      const dateStr = date.format('YYYY-MM-DD');
      const isToday = date.isSame(dayjs(), 'day');
      const isSelected = dateStr === selectedDayProg;
      const dayAppts = appointments.filter(a => dayjs(a.dataProgramare).isSame(date, 'day'));

      days.push(
        <div
          key={dateStr}
          onClick={() => setSelectedDayProg(dateStr)}
          className={`calendar-cell border-end border-bottom border-light p-1 p-md-2 position-relative transition-all cursor-pointer ${
            isSelected ? 'bg-soft-primary border-primary border-2 shadow-inner' : 'hover-bg-light'
          }`}
          style={{ cursor: 'pointer' }}
        >
          <div className="d-flex justify-content-between align-items-start mb-1">
            <span
              className={`calendar-day-number fw-bold rounded-pill ${
                isToday ? 'bg-primary text-white' : isSelected ? 'bg-white text-primary border border-primary' : 'text-secondary'
              }`}
            >
              {d}
            </span>
            {dayAppts.length > 0 && (
              <span className="badge rounded-pill bg-soft-primary small p-1 p-md-2" style={{ fontSize: '0.65rem' }}>
                {dayAppts.length}
              </span>
            )}
          </div>
          <div className="calendar-events-container overflow-hidden">
            {/* Desktop: Text Pills */}
            <div className="d-none d-md-block">
              {dayAppts.map((a, idx) => (
                <div
                  key={a.id || idx}
                  className="calendar-event-pill bg-soft-info mb-1 truncate small px-2 rounded-pill border-start border-primary"
                  style={{ fontSize: '0.7rem' }}
                >
                  <span className="fw-bold">{dayjs(a.dataProgramare).format('HH:mm')}</span> {a.medic?.user?.lastName || 'Medic'}
                </div>
              ))}
            </div>

            {/* Mobile: Simple Dots */}
            <div className="d-flex d-md-none justify-content-center gap-1 flex-wrap pt-1">
              {dayAppts.slice(0, 3).map((a, idx) => (
                <div key={idx} className="bg-primary rounded-circle" style={{ width: '6px', height: '6px' }} />
              ))}
              {dayAppts.length > 3 && (
                <div className="text-primary fw-bold" style={{ fontSize: '0.6rem', lineHeight: '1' }}>
                  +
                </div>
              )}
            </div>
          </div>
        </div>,
      );
    }

    // Zilele lunii URMATOARE (doar pana la finalul randului curent)
    const remainingInRow = days.length % 7 === 0 ? 0 : 7 - (days.length % 7);
    for (let i = 1; i <= remainingInRow; i++) {
      days.push(
        <div key={`next-${i}`} className="calendar-cell bg-light bg-opacity-10 border-end border-bottom border-light p-1 p-md-2 opacity-25">
          <div className="text-secondary small">{i}</div>
        </div>,
      );
    }

    return days;
  };

  return (
    <div className="container py-4 fade-in-up">
      {notifiedWaitlist.map(w => (
        <div key={w.id} className="alert bg-soft-success border-0 rounded-4 p-3 mb-4 shadow-sm animate-pulse-subtle">
          <div className="d-flex align-items-center">
            <div className="bg-success text-white rounded-circle p-2 me-3">
              <FontAwesomeIcon icon="bell" />
            </div>
            <div className="flex-grow-1">
              <h6 className="fw-bold mb-1">Loc disponibil găsit!</h6>
              <p className="mb-0 small">
                Un loc s-a eliberat pentru data de{' '}
                <strong>{dayjs(w.notifiedSlotTime ?? w.dataPreferata).format('DD MMMM YYYY, HH:mm')}</strong>.
              </p>
            </div>
            <button className="btn btn-success btn-sm rounded-pill px-4 fw-bold shadow-sm" onClick={() => handleClaimSpot(w.id)}>
              Rezervă Acum
            </button>
          </div>
        </div>
      ))}

      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-4 mb-4 pt-2">
        <div>
          <h2 className="fw-bold text-dark mb-1 h3">Bun venit, {account?.lastName || account?.login}!</h2>
          <p className="text-muted mb-0 opacity-75">Iată situația sănătății tale pentru ziua de azi.</p>
        </div>
        <div className="mt-2 mt-md-0">
          <Link
            to="/pacient-page"
            className="btn btn-primary rounded-pill px-4 py-2 shadow-sm fw-bold d-inline-flex align-items-center transition-all hover-lift"
          >
            <FontAwesomeIcon icon="plus" className="me-2" />
            Programare Nouă
          </Link>
        </div>
      </div>

      <div className="row">
        <div className="col-lg-8">
          <div className="card glass-panel border-0 mb-4 overflow-hidden shadow-sm">
            <div className="card-header bg-transparent border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
              <h4 className="mb-0 fw-bold text-primary">{currentMonth.format('MMMM YYYY')}</h4>
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
                <button
                  className="btn btn-sm btn-light rounded-pill border-0"
                  onClick={() => setCurrentMonth(prev => prev.add(1, 'month'))}
                >
                  ❯
                </button>
              </div>
            </div>
            <div className="card-body p-0">
              <div className="calendar-grid">
                {['LUN', 'MAR', 'MIE', 'JOI', 'VIN', 'SÂM', 'DUM'].map(d => (
                  <div
                    key={d}
                    className="calendar-day-header text-center py-2 fw-bold text-secondary border-bottom border-end border-light px-0"
                    style={{ minWidth: 0 }}
                  >
                    <span className="d-none d-sm-inline" style={{ fontSize: '0.75rem' }}>
                      {d}
                    </span>
                    <span className="d-inline d-sm-none" style={{ fontSize: '0.7rem' }}>
                      {d.charAt(0)}
                    </span>
                  </div>
                ))}
                {daysInMonth()}
              </div>
            </div>
          </div>
        </div>
        <div className="col-lg-4">
          <div className="card glass-panel border-0 shadow-sm rounded-4 overflow-hidden h-100">
            <div className="card-header bg-primary text-white py-3 px-4">
              <h5 className="mb-0 fw-bold">
                <FontAwesomeIcon icon="capsules" className="me-2" />
                Tratamentul Meu Zilnic
              </h5>
            </div>
            <div className="card-body p-4">
              {todayDoses.length === 0 ? (
                <div className="text-center py-4 opacity-50">
                  <FontAwesomeIcon icon="calendar-check" size="3x" className="mb-3" />
                  <p className="mb-0">Nu ai doze programate pentru azi.</p>
                </div>
              ) : (
                <div className="medication-checklist">
                  {todayDoses.map(dose => (
                    <div
                      key={dose.id}
                      className={`dose-item p-3 rounded-3 mb-3 border-start border-4 transition-all ${
                        dose.status === 'TAKEN' ? 'bg-light border-success opacity-75' : 'bg-white border-warning shadow-sm'
                      }`}
                    >
                      <div className="d-flex justify-content-between align-items-center">
                        <div>
                          <p className="mb-0 fw-bold text-dark">{dose.medicament}</p>
                          <small className="text-secondary">
                            <FontAwesomeIcon icon="clock" className="me-1" />
                            {dayjs(dose.oraPlanificata).format('HH:mm')}
                          </small>
                        </div>
                        {dose.status !== 'TAKEN' ? (
                          <button className="btn btn-sm btn-outline-primary rounded-pill px-3" onClick={() => handleConfirmDose(dose.id)}>
                            Bifează
                          </button>
                        ) : (
                          <span className="text-success small fw-bold">
                            <FontAwesomeIcon icon="check-circle" className="me-1" />
                            Luat
                          </span>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}

              <div className="mt-4 pt-3 border-top">
                <h6 className="fw-bold mb-3 small text-uppercase text-secondary">Progres Tratament</h6>
                <div className="progress rounded-pill shadow-inner" style={{ height: '8px' }}>
                  <div
                    className="progress-bar bg-success"
                    style={{
                      width: `${
                        medicationDoses.length > 0
                          ? (medicationDoses.filter(d => d.status === 'TAKEN').length / medicationDoses.length) * 100
                          : 0
                      }%`,
                    }}
                  />
                </div>
                <div className="d-flex justify-content-between mt-2 small text-muted">
                  <span>{medicationDoses.filter(d => d.status === 'TAKEN').length} doze luate</span>
                  <span>{medicationDoses.length} total</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Secțiunea Detalii Programare la Click */}
      {selectedDayProg && (
        <div className="mt-5 mb-5 p-4 glass-panel border-0 shadow-sm rounded-4 fade-in-up">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h4 className="fw-bold text-dark mb-0">
              <FontAwesomeIcon icon="calendar-day" className="text-primary me-2" />
              Programări pentru {dayjs(selectedDayProg).format('DD MMMM YYYY')}
            </h4>
            <button className="btn btn-sm btn-light rounded-pill px-3" onClick={() => setSelectedDayProg(null)}>
              Închide
            </button>
          </div>

          {appointments.filter(a => dayjs(a.dataProgramare).format('YYYY-MM-DD') === selectedDayProg).length === 0 ? (
            <div className="text-center py-5 bg-light bg-opacity-50 rounded-4">
              <div className="opacity-50">
                <FontAwesomeIcon icon="info-circle" size="2x" className="mb-2" />
                <p className="mb-0">Nicio programare înregistrată pentru această zi.</p>
              </div>
            </div>
          ) : (
            <div className="row g-3">
              {appointments
                .filter(a => dayjs(a.dataProgramare).format('YYYY-MM-DD') === selectedDayProg)
                .sort((a, b) => (a.dataProgramare < b.dataProgramare ? -1 : 1))
                .map(a => (
                  <div key={a.id} className="col-md-6 col-lg-4">
                    <div className="p-3 bg-white rounded-4 border border-light-subtle shadow-xs h-100 hover-lift transition-all border-start border-4 border-start-primary">
                      <div className="d-flex justify-content-between align-items-start mb-3">
                        <div className="bg-soft-primary px-3 py-1 rounded-pill fw-bold text-primary small">
                          <FontAwesomeIcon icon="clock" className="me-2" />
                          {dayjs(a.dataProgramare).format('HH:mm')}
                        </div>
                        <span className="badge bg-soft-success rounded-pill small-text px-2">{a.status}</span>
                      </div>

                      <div className="mb-3">
                        <div className="small text-uppercase text-muted fw-bold mb-1">Medic Curant</div>
                        <div className="fw-bold text-dark">
                          {a.medic?.user ? `Dr. ${a.medic.user.lastName} ${a.medic.user.firstName}` : '-'}
                        </div>
                        <div className="text-primary small fw-semibold">{a.medic?.gradProfesional}</div>
                        <div className="text-secondary small mt-1">
                          <FontAwesomeIcon icon="stethoscope" className="me-2" />
                          {a.medic?.sectie?.nume || 'Secție Generală'}
                        </div>
                      </div>

                      <div className="pt-2 border-top">
                        <div className="small text-uppercase text-muted fw-bold mb-1">Locație & Clinică</div>
                        <div className="fw-bold text-dark small">{a.clinica?.nume}</div>
                        {(a.clinica?.locatie?.oras || a.clinica?.locatie?.adresa) && (
                          <div className="text-muted small mt-1">
                            <FontAwesomeIcon icon="map-marker-alt" className="me-2 text-primary opacity-75" />
                            {a.clinica?.locatie?.oras}
                            {a.clinica?.locatie?.oras && a.clinica?.locatie?.adresa ? ', ' : ''}
                            {a.clinica?.locatie?.adresa}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
            </div>
          )}
        </div>
      )}

      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Se încarcă...</span>
          </div>
        </div>
      )}

      <style>{`
        .calendar-cell {
          min-height: 120px;
        }
        .calendar-grid {
          display: grid;
          grid-template-columns: repeat(7, 1fr);
          background: #fff;
          width: 100%;
        }
        .calendar-cell {
          border-right: 1px solid #f0f0f0;
          border-bottom: 1px solid #f0f0f0;
          transition: background 0.2s;
        }
        .calendar-event-pill {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .bg-soft-info {
          background-color: rgba(13, 202, 240, 0.1);
          color: #087990;
        }
        .bg-soft-success {
          background-color: rgba(25, 135, 84, 0.1);
          color: #0f5132;
        }
        .bg-soft-primary {
          background-color: rgba(13, 110, 253, 0.1);
          color: #084298;
        }
        .animate-pulse-subtle {
          animation: pulse 2s infinite ease-in-out;
        }
        @keyframes pulse {
          0% { transform: scale(1); }
          50% { transform: scale(1.01); }
          100% { transform: scale(1); }
        }
        .shadow-inner {
          box-shadow: inset 0 2px 4px rgba(0,0,0,0.05);
        }
        .transition-all {
          transition: all 0.2s ease-in-out;
        }

        /* Mobile Optimizations */
        @media (max-width: 768px) {
          .calendar-cell {
            min-height: 50px !important;
            padding: 2px !important;
          }
          .calendar-day-number {
             font-size: 0.75rem !important;
             padding: 2px 6px !important;
          }
          .calendar-day-header {
            padding: 6px 0 !important;
            font-size: 0.65rem !important;
            color: #adb5bd;
          }
          .card-header h4 {
            font-size: 1rem !important;
          }
          .calendar-events-container {
            min-height: 10px;
            margin-top: 0 !important;
          }
          .btn-group.shadow-sm {
             transform: scale(0.8);
          }
          .container {
            padding-left: 0px !important;
            padding-right: 0px !important;
            max-width: 100% !important;
          }
          .card-body.p-4 {
            padding: 0.5rem !important;
          }
        }

        @media (max-width: 576px) {
           .calendar-cell {
            min-height: 45px !important;
          }
          h2 {
            font-size: 1.3rem !important;
          }
          .card-body.p-4 {
            padding: 1rem !important;
          }
        }
      `}</style>
    </div>
  );
}
