import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function nonBlank(control: AbstractControl): ValidationErrors | null {
  return typeof control.value === 'string' && control.value.trim().length === 0
    ? { blank: true }
    : null;
}

export function pastDate(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) {
    return null;
  }

  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) {
    return { dateInvalid: true };
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date < today ? null : { pastDate: true };
}

export const futureDateTime: ValidatorFn = (control) => {
  const value = control.value;
  if (!value) {
    return null;
  }

  const date = new Date(value);
  return !Number.isNaN(date.getTime()) && date.getTime() > Date.now()
    ? null
    : { futureDateTime: true };
};