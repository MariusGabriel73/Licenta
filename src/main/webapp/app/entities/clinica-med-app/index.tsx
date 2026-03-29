import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ClinicaMedApp from './clinica-med-app';
import ClinicaMedAppDetail from './clinica-med-app-detail';
import ClinicaMedAppUpdate from './clinica-med-app-update';
import ClinicaMedAppDeleteDialog from './clinica-med-app-delete-dialog';

const ClinicaMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ClinicaMedApp />} />
    <Route path="new" element={<ClinicaMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<ClinicaMedAppDetail />} />
      <Route path="edit" element={<ClinicaMedAppUpdate />} />
      <Route path="delete" element={<ClinicaMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClinicaMedAppRoutes;
