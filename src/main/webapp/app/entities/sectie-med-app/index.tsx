import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import SectieMedApp from './sectie-med-app';
import SectieMedAppDetail from './sectie-med-app-detail';
import SectieMedAppUpdate from './sectie-med-app-update';
import SectieMedAppDeleteDialog from './sectie-med-app-delete-dialog';

const SectieMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<SectieMedApp />} />
    <Route path="new" element={<SectieMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<SectieMedAppDetail />} />
      <Route path="edit" element={<SectieMedAppUpdate />} />
      <Route path="delete" element={<SectieMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SectieMedAppRoutes;
