import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IPacientMedApp {
  id?: number;
  cnp?: string;
  telefon?: string | null;
  dataNasterii?: dayjs.Dayjs | null;
  adresa?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IPacientMedApp> = {};
