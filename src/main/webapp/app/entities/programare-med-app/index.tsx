import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProgramareMedApp from './programare-med-app';
import ProgramareMedAppDetail from './programare-med-app-detail';
import ProgramareMedAppUpdate from './programare-med-app-update';
import ProgramareMedAppDeleteDialog from './programare-med-app-delete-dialog';

const ProgramareMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProgramareMedApp />} />
    <Route path="new" element={<ProgramareMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<ProgramareMedAppDetail />} />
      <Route path="edit" element={<ProgramareMedAppUpdate />} />
      <Route path="delete" element={<ProgramareMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProgramareMedAppRoutes;
