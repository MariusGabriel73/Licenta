import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SpecializareMedApp from './specializare-med-app';
import SpecializareMedAppDetail from './specializare-med-app-detail';
import SpecializareMedAppUpdate from './specializare-med-app-update';
import SpecializareMedAppDeleteDialog from './specializare-med-app-delete-dialog';

const SpecializareMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SpecializareMedApp />} />
    <Route path="new" element={<SpecializareMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<SpecializareMedAppDetail />} />
      <Route path="edit" element={<SpecializareMedAppUpdate />} />
      <Route path="delete" element={<SpecializareMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SpecializareMedAppRoutes;
