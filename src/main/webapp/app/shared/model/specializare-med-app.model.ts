import { IMedicMedApp } from 'app/shared/model/medic-med-app.model';

export interface ISpecializareMedApp {
  id?: number;
  nume?: string;
  medicis?: IMedicMedApp[] | null;
}

export const defaultValue: Readonly<ISpecializareMedApp> = {};
