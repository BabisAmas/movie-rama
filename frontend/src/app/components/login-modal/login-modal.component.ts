import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss'],
})
export class LoginModalComponent {
  user = {
    email: '',
    password: '',
  };

  @Output() closeModalEvent = new EventEmitter<void>();

  onSubmit() {
    this.closeModal();
  }

  closeModal() {
    this.closeModalEvent.emit();
  }
}
