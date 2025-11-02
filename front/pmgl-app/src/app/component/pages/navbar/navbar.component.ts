import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'] 
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