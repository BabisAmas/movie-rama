import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { CustomValidators } from 'src/app/validators/custom-validators';

@Component({
  selector: 'app-registration-modal',
  templateUrl: './registration-modal.component.html',
  styleUrls: ['./registration-modal.component.scss'],
})
export class RegistrationModalComponent {
  @Output() registrationSuccess = new EventEmitter<void>();
  display = true;
  registrationForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private messageService: MessageService
  ) {
    this.registrationForm = this.fb.group({
      username: [
        '',
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(50),
        ],
      ],
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          CustomValidators.passwordComplexityValidator(),
        ],
      ],
      firstname: ['', [Validators.required, Validators.maxLength(100)]],
      lastname: ['', [Validators.required, Validators.maxLength(100)]],
    });
  }

  get f() {
    return this.registrationForm.controls;
  }

  hasError(controlName: string, errorName: string): boolean {
    return this.registrationForm.controls[controlName]?.hasError(errorName);
  }

  onSubmit() {
    if (!this.registrationForm.valid) {
      return;
    }

    const newUser = new User(
      this.registrationForm.value.username,
      this.registrationForm.value.email,
      this.registrationForm.value.password,
      this.registrationForm.value.firstname,
      this.registrationForm.value.lastname
    );

    this.userService.register(newUser).subscribe({
      next: () => {
        this.registrationSuccess.emit();
        this.messageService.add({
          severity: 'success',
          summary: 'Registration Successful',
          detail: 'You have been registered successfully!',
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Registration Failed',
          detail: 'Unable to register. Please try again later.',
        });
      },
    });
  }
}
