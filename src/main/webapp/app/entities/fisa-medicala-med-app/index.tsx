import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import FisaMedicalaMedApp from './fisa-medicala-med-app';
import FisaMedicalaMedAppDetail from './fisa-medicala-med-app-detail';
import FisaMedicalaMedAppUpdate from './fisa-medicala-med-app-update';
import FisaMedicalaMedAppDeleteDialog from './fisa-medicala-med-app-delete-dialog';

const FisaMedicalaMedAppRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<FisaMedicalaMedApp />} />
    <Route path="new" element={<FisaMedicalaMedAppUpdate />} />
    <Route path=":id">
      <Route index element={<FisaMedicalaMedAppDetail />} />
      <Route path="edit" element={<FisaMedicalaMedAppUpdate />} />
      <Route path="delete" element={<FisaMedicalaMedAppDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FisaMedicalaMedAppRoutes;
