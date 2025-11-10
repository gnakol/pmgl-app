import { AccountDTO } from "../../customer-package/beans/account-dto.model";

export type AdminRole = 'SUPER_ADMIN' | 'ADMIN' | 'COMMERCIAL' | 'TECH';

export interface AdminDTO {
  idAdmin: number;
  zone?: string | null;
  account: AccountDTO;
  refAdmins?: string | null;
  role: AdminRole;
  creationDate: string; // ISO
}
