import dayjs from 'dayjs';
import { IProgramareMedApp } from 'app/shared/model/programare-med-app.model';

export interface IFisaMedicalaMedApp {
  id?: number;
  diagnostic?: string | null;
  tratament?: string | null;
  recomandari?: string | null;
  dataConsultatie?: dayjs.Dayjs | null;
  programare?: IProgramareMedApp | null;
}

export const defaultValue: Readonly<IFisaMedicalaMedApp> = {};
