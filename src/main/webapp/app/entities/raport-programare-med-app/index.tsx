import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import RaportProgramareMedApp from './raport-programare-med-app';
import RaportProgramareMedAppDetail from './raport-programare-med-app-detail';
import RaportProgramareMedAppUpdate from './raport-programare-med-app-update';
import RaportProgramareMedAppDeleteDialog from './raport-programare-med-app-delete-dialog';

const RaportProgramareMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<RaportProgramareMedApp />} />
    <Route path="new" element={<RaportProgramareMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<RaportProgramareMedAppDetail />} />
      <Route path="edit" element={<RaportProgramareMedAppUpdate />} />
      <Route path="delete" element={<RaportProgramareMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RaportProgramareMedAppRoutes;
