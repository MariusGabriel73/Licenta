import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProgramMedApp from './program-med-app';
import ProgramMedAppDetail from './program-med-app-detail';
import ProgramMedAppUpdate from './program-med-app-update';
import ProgramMedAppDeleteDialog from './program-med-app-delete-dialog';

const ProgramMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProgramMedApp />} />
    <Route path="new" element={<ProgramMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<ProgramMedAppDetail />} />
      <Route path="edit" element={<ProgramMedAppUpdate />} />
      <Route path="delete" element={<ProgramMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProgramMedAppRoutes;
