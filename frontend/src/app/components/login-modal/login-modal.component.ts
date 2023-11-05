import { Component, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AuthService } from 'src/app/services/auth.service';


@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss'],
})
export class LoginModalComponent {
  @Output() closeModalEvent = new EventEmitter<void>();
  loginForm: FormGroup;
  display = true;

  constructor(private fb: FormBuilder, private authService: AuthService, private messageService: MessageService) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onLogin() {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      this.authService.login(username, password).subscribe({
        next: () => {
          this.display = false;
          this.closeModalEvent.emit();
          this.messageService.add({ severity: 'success', summary: 'Login Successful', detail: 'Welcome back!' });
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Login Failed', detail: 'Invalid credentials' });
        }
      });
    }
  }
}
