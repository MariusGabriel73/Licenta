import React, { useEffect, useState } from 'react';
import { Navigate, Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { Programare, getCurrentPacientByUserId, getMyAppointments, type ID } from 'app/shared/api/pacient-api';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export default function HomePage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const [loading, setLoading] = useState(false);
  const [pacientId, setPacientId] = useState<ID | undefined>(undefined);
  const [appointments, setAppointments] = useState<Programare[]>([]);
  const [currentMonth, setCurrentMonth] = useState<dayjs.Dayjs>(dayjs().startOf('month'));

  useEffect(() => {
    (async () => {
      if (isAuthenticated && account?.id && account.authorities?.includes(AUTHORITIES.PACIENT)) {
        setLoading(true);
        try {
          const p = await getCurrentPacientByUserId(account.id);
          if (p?.id) {
            setPacientId(p.id);
            const my = await getMyAppointments(p.id);
            setAppointments(my);
          }
        } finally {
          setLoading(false);
        }
      }
    })();
  }, [isAuthenticated, account]);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (account && account.authorities) {
    if (account.authorities.includes(AUTHORITIES.MEDIC)) {
      return <Navigate to="/medic" replace />;
    }
    // Admin goes to entities/admin or stays here? Let's redirect to admin for now or keep generic.
    if (account.authorities.includes(AUTHORITIES.ADMIN)) {
      return <Navigate to="/admin" replace />;
    }
  }

  // CALENDAR LOGIC (Simplified from MedicPage)
  const isSelected = (dateStr: string) => false; // not needed for home overview yet

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
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 fw-bold text-dark">Bun venit, {account?.firstName || account?.login}!</h2>
          <p className="text-secondary">Iată situația programărilor tale pentru luna aceasta.</p>
        </div>
        <Link to="/pacient" className="btn btn-primary rounded-pill shadow-sm px-4">
          <FontAwesomeIcon icon="plus" className="me-2" />
          Programare Nouă
        </Link>
      </div>

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
                className="calendar-day-header py-3 text-center text-muted small fw-bold text-uppercase border-bottom border-light bg-light bg-opacity-50"
              >
                {d}
              </div>
            ))}
            {daysInMonth()}
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

      {!loading && appointments.length === 0 && (
        <div className="alert bg-soft-primary border-0 rounded-4 p-4 text-center">
          <h5 className="fw-bold mb-2">Nu ai nicio programare viitoare</h5>
          <p className="mb-3 text-secondary">Apasă pe butonul de mai sus pentru a programa o consultație.</p>
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
        .hover-bg-light:hover {
          background-color: #fbfbfb;
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
      `}</style>
    </div>
  );
}
