export interface ILocatieMedApp {
  id?: number;
  oras?: string | null;
  adresa?: string | null;
  codPostal?: string | null;
}

export const defaultValue: Readonly<ILocatieMedApp> = {};
