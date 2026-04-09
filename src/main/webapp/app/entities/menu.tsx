import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/pacient-med-app">
        <Translate contentKey="global.menu.entities.pacientMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/medic-med-app">
        <Translate contentKey="global.menu.entities.medicMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/specializare-med-app">
        <Translate contentKey="global.menu.entities.specializareMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/clinica-med-app">
        <Translate contentKey="global.menu.entities.clinicaMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/locatie-med-app">
        <Translate contentKey="global.menu.entities.locatieMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/program-med-app">
        <Translate contentKey="global.menu.entities.programMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/programare-med-app">
        <Translate contentKey="global.menu.entities.programareMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/fisa-medicala-med-app">
        <Translate contentKey="global.menu.entities.fisaMedicalaMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/raport-programare-med-app">
        <Translate contentKey="global.menu.entities.raportProgramareMedApp" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/sectie-med-app">
        <Translate contentKey="global.menu.entities.sectieMedApp" />
      </MenuItem>
      <MenuItem icon="flask" to="/analize-medicale">
        🔬 Analize Medicale
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
