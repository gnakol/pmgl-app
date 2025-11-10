export interface AccountDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  civility: 'MR' | 'MRS' | 'MS';
}