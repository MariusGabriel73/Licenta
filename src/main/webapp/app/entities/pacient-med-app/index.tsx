import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PacientMedApp from './pacient-med-app';
import PacientMedAppDetail from './pacient-med-app-detail';
import PacientMedAppUpdate from './pacient-med-app-update';
import PacientMedAppDeleteDialog from './pacient-med-app-delete-dialog';

const PacientMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<PacientMedApp />} />
    <Route path="new" element={<PacientMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<PacientMedAppDetail />} />
      <Route path="edit" element={<PacientMedAppUpdate />} />
      <Route path="delete" element={<PacientMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PacientMedAppRoutes;
