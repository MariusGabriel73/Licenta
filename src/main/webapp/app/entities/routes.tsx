import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import PacientMedApp from './pacient-med-app';
import MedicMedApp from './medic-med-app';
import SpecializareMedApp from './specializare-med-app';
import ClinicaMedApp from './clinica-med-app';
import LocatieMedApp from './locatie-med-app';
import ProgramMedApp from './program-med-app';
import ProgramareMedApp from './programare-med-app';
import FisaMedicalaMedApp from './fisa-medicala-med-app';
import RaportProgramareMedApp from './raport-programare-med-app';
import SectieMedApp from './sectie-med-app';
import { AnalizeMedicale } from './rezultat-analiza-med-app';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="pacient-med-app/*" element={<PacientMedApp />} />
        <Route path="medic-med-app/*" element={<MedicMedApp />} />
        <Route path="specializare-med-app/*" element={<SpecializareMedApp />} />
        <Route path="clinica-med-app/*" element={<ClinicaMedApp />} />
        <Route path="locatie-med-app/*" element={<LocatieMedApp />} />
        <Route path="program-med-app/*" element={<ProgramMedApp />} />
        <Route path="programare-med-app/*" element={<ProgramareMedApp />} />
        <Route path="fisa-medicala-med-app/*" element={<FisaMedicalaMedApp />} />
        <Route path="raport-programare-med-app/*" element={<RaportProgramareMedApp />} />
        <Route path="sectie-med-app/*" element={<SectieMedApp />} />
        <Route path="analize-medicale" element={<AnalizeMedicale />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
