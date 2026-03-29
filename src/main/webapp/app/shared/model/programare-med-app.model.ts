import dayjs from 'dayjs';
import { IPacientMedApp } from 'app/shared/model/pacient-med-app.model';
import { IMedicMedApp } from 'app/shared/model/medic-med-app.model';
import { IClinicaMedApp } from 'app/shared/model/clinica-med-app.model';
import { ProgramareStatus } from 'app/shared/model/enumerations/programare-status.model';

export interface IProgramareMedApp {
  id?: number;
  dataProgramare?: dayjs.Dayjs | null;
  status?: keyof typeof ProgramareStatus | null;
  observatii?: string | null;
  pacient?: IPacientMedApp | null;
  medic?: IMedicMedApp | null;
  clinica?: IClinicaMedApp | null;
}

export const defaultValue: Readonly<IProgramareMedApp> = {};
