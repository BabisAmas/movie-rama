import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static passwordComplexityValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }

      const hasUpperCase = /[A-Z]/.test(value);
      const hasLowerCase = /[a-z]/.test(value);
      const hasNumber = /\d/.test(value);
      const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

      const isValid = hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar;

      if (!isValid) {
        return {
          passwordComplexity: {
            valid: false,
            rules: {
              hasUpperCase,
              hasLowerCase,
              hasNumber,
              hasSpecialChar
            }
          }
        };
      }

      return null;
    };
  }
}