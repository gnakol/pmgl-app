import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  showQuoteModal = false;

  openQuoteModal() {
    this.showQuoteModal = true;
  }

  closeQuoteModal() {
    this.showQuoteModal = false;
  }
}