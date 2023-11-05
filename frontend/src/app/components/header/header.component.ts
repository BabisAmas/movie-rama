import { Component } from '@angular/core';
import { MessageService } from 'primeng/api';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  showModal = false;

  constructor(private authService: AuthService, private messageService: MessageService) {}

  get isLoggedIn() {
    return this.authService.isLoggedIn();
  }

  logout() {
    this.authService.logout();

    this.messageService.add({
      severity: 'success',
      summary: 'Logged Out',
      detail: 'You have been logged out successfully.'
    });
  }

  toggleModal() {
    this.showModal = !this.showModal;
  }
}
