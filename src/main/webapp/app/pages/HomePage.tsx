import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export default function HomePage() {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (account && account.authorities) {
    if (account.authorities.includes(AUTHORITIES.MEDIC)) {
      return <Navigate to="/medic" replace />;
    }
    if (account.authorities.includes(AUTHORITIES.PACIENT)) {
      return <Navigate to="/pacient" replace />;
    }
    // Optional fallback for admin
    if (account.authorities.includes(AUTHORITIES.ADMIN)) {
      return <Navigate to="/admin" replace />;
    }
  }

  // Fallback if the user has no recognized role or just USER
  return (
    <div className="container py-5 text-center">
      <h1>Bine ai venit!</h1>
      <p>Contul tău nu are un rol specificat (Medic sau Pacient).</p>
    </div>
  );
}
