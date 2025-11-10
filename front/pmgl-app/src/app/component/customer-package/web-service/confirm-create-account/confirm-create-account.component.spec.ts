import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmCreateAccountComponent } from './confirm-create-account.component';

describe('ConfirmCreateAccountComponent', () => {
  let component: ConfirmCreateAccountComponent;
  let fixture: ComponentFixture<ConfirmCreateAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmCreateAccountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmCreateAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
