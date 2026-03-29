import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import LocatieMedApp from './locatie-med-app';
import LocatieMedAppDetail from './locatie-med-app-detail';
import LocatieMedAppUpdate from './locatie-med-app-update';
import LocatieMedAppDeleteDialog from './locatie-med-app-delete-dialog';

const LocatieMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<LocatieMedApp />} />
    <Route path="new" element={<LocatieMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<LocatieMedAppDetail />} />
      <Route path="edit" element={<LocatieMedAppUpdate />} />
      <Route path="delete" element={<LocatieMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LocatieMedAppRoutes;
