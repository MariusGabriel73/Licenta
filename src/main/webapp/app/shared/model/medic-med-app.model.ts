import { IUser } from 'app/shared/model/user.model';
import { ISectieMedApp } from 'app/shared/model/sectie-med-app.model';
import { IClinicaMedApp } from 'app/shared/model/clinica-med-app.model';
import { ISpecializareMedApp } from 'app/shared/model/specializare-med-app.model';

export interface IMedicMedApp {
  id?: number;
  gradProfesional?: string | null;
  telefon?: string | null;
  disponibil?: boolean | null;
  user?: IUser | null;
  sectie?: ISectieMedApp | null;
  clinicis?: IClinicaMedApp[] | null;
  specializaris?: ISpecializareMedApp[] | null;
}

export const defaultValue: Readonly<IMedicMedApp> = {
  disponibil: false,
};
