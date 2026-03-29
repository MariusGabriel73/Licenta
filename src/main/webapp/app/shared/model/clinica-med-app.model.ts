import { ILocatieMedApp } from 'app/shared/model/locatie-med-app.model';
import { IMedicMedApp } from 'app/shared/model/medic-med-app.model';

export interface IClinicaMedApp {
  id?: number;
  nume?: string;
  telefon?: string | null;
  email?: string | null;
  locatie?: ILocatieMedApp | null;
  medicis?: IMedicMedApp[] | null;
}

export const defaultValue: Readonly<IClinicaMedApp> = {};
