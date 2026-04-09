import pacient from 'app/entities/pacient-med-app/pacient-med-app.reducer';
import medic from 'app/entities/medic-med-app/medic-med-app.reducer';
import specializare from 'app/entities/specializare-med-app/specializare-med-app.reducer';
import clinica from 'app/entities/clinica-med-app/clinica-med-app.reducer';
import locatie from 'app/entities/locatie-med-app/locatie-med-app.reducer';
import program from 'app/entities/program-med-app/program-med-app.reducer';
import programare from 'app/entities/programare-med-app/programare-med-app.reducer';
import fisaMedicala from 'app/entities/fisa-medicala-med-app/fisa-medicala-med-app.reducer';
import raportProgramare from 'app/entities/raport-programare-med-app/raport-programare-med-app.reducer';
import sectie from 'app/entities/sectie-med-app/sectie-med-app.reducer';
import rezultatAnaliza from 'app/entities/rezultat-analiza-med-app/rezultat-analiza.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  pacient,
  medic,
  specializare,
  clinica,
  locatie,
  program,
  programare,
  fisaMedicala,
  raportProgramare,
  sectie,
  rezultatAnaliza,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
