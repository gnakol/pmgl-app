import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientInvitePageComponent } from './client-invite-page.component';

describe('ClientInvitePageComponent', () => {
  let component: ClientInvitePageComponent;
  let fixture: ComponentFixture<ClientInvitePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClientInvitePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientInvitePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
