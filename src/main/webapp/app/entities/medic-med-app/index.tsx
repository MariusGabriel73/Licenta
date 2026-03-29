import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MedicMedApp from './medic-med-app';
import MedicMedAppDetail from './medic-med-app-detail';
import MedicMedAppUpdate from './medic-med-app-update';
import MedicMedAppDeleteDialog from './medic-med-app-delete-dialog';

const MedicMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MedicMedApp />} />
    <Route path="new" element={<MedicMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<MedicMedAppDetail />} />
      <Route path="edit" element={<MedicMedAppUpdate />} />
      <Route path="delete" element={<MedicMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MedicMedAppRoutes;
