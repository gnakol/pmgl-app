import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevisModalComponent } from './devis-modal.component';

describe('DevisModalComponent', () => {
  let component: DevisModalComponent;
  let fixture: ComponentFixture<DevisModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DevisModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevisModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
