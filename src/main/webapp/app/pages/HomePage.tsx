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

export default function HomePage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const [loading, setLoading] = useState(false);
  const [pacientId, setPacientId] = useState<ID | undefined>(undefined);
  const [appointments, setAppointments] = useState<Programare[]>([]);
  const [currentMonth, setCurrentMonth] = useState<dayjs.Dayjs>(dayjs().startOf('month'));

  const [medicationDoses, setMedicationDoses] = useState<any[]>([]);
  const [waitlistEntries, setWaitlistEntries] = useState<any[]>([]);

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
    const startDay = (startOfMonth.day() + 6) % 7; // Monday start

    for (let i = 0; i < startDay; i++) {
      days.push(<div key={`prev-${i}`} className="calendar-cell bg-light bg-opacity-10 border-end border-bottom border-light" />);
    }

    for (let d = 1; d <= endOfMonth.date(); d++) {
      const date = startOfMonth.date(d);
      const dateStr = date.format('YYYY-MM-DD');
      const isToday = dayjs().format('YYYY-MM-DD') === dateStr;

      const dayAppts = appointments.filter(a => dayjs(a.dataProgramare).format('YYYY-MM-DD') === dateStr);

      days.push(
        <div
          key={dateStr}
          className="calendar-cell border-end border-bottom border-light p-2 position-relative hover-bg-light transition-all"
          style={{ minHeight: '120px' }}
        >
          <div className="d-flex justify-content-between align-items-start mb-1">
            <span className={`small fw-bold px-2 py-1 rounded-pill ${isToday ? 'bg-primary text-white' : 'text-secondary'}`}>{d}</span>
            {dayAppts.length > 0 && (
              <span className="badge rounded-pill bg-soft-primary small" style={{ fontSize: '0.65rem' }}>
                {dayAppts.length}
              </span>
            )}
          </div>
          <div className="calendar-events-container overflow-hidden">
            {dayAppts.map((a, idx) => (
              <div
                key={a.id || idx}
                className="calendar-event-pill bg-soft-info mb-1 truncate small px-2 rounded-pill border-start border-primary"
                style={{ fontSize: '0.7rem', borderLeftWidth: '3px !important' }}
              >
                <span className="fw-bold">{dayjs(a.dataProgramare).format('HH:mm')}</span> {a.medic?.user?.lastName || 'Medic'}
              </div>
            ))}
          </div>
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

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 fw-bold text-dark">Bun venit, {account?.firstName || account?.login}!</h2>
          <p className="text-secondary">Iată situația sănătății tale pentru ziua de azi.</p>
        </div>
        <Link to="/pacient" className="btn btn-primary rounded-pill shadow-sm px-4 fw-semibold">
          <FontAwesomeIcon icon="plus" className="me-2" />
          Programare Nouă
        </Link>
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
                {['Lun', 'Mar', 'Mie', 'Joi', 'Vin', 'Sâm', 'Dum'].map(d => (
                  <div
                    key={d}
                    className="calendar-day-header py-3 text-center text-muted small fw-bold text-uppercase border-bottom border-light bg-light bg-opacity-50"
                  >
                    {d}
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

      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Se încarcă...</span>
          </div>
        </div>
      )}

      <style>{`
        .calendar-grid {
          display: grid;
          grid-template-columns: repeat(7, 1fr);
          background: #fff;
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
      `}</style>
    </div>
  );
}
