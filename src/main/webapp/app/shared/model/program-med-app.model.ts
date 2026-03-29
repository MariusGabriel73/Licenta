import dayjs from 'dayjs';
import { IMedicMedApp } from 'app/shared/model/medic-med-app.model';
import { ILocatieMedApp } from 'app/shared/model/locatie-med-app.model';

export interface IProgramMedApp {
  id?: number;
  ziuaSaptamanii?: string | null;
  oraStart?: dayjs.Dayjs | null;
  oraFinal?: dayjs.Dayjs | null;
  medic?: IMedicMedApp | null;
  locatie?: ILocatieMedApp | null;
}

export const defaultValue: Readonly<IProgramMedApp> = {};
