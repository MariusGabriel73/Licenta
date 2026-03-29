import dayjs from 'dayjs';
import { IProgramareMedApp } from 'app/shared/model/programare-med-app.model';

export interface IRaportProgramareMedApp {
  id?: number;
  oraProgramata?: dayjs.Dayjs | null;
  oraInceputConsultatie?: dayjs.Dayjs | null;
  durataConsultatie?: number | null;
  programare?: IProgramareMedApp | null;
}

export const defaultValue: Readonly<IRaportProgramareMedApp> = {};
