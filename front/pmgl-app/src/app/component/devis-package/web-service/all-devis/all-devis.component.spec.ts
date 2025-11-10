import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllDevisComponent } from './all-devis.component';

describe('AllDevisComponent', () => {
  let component: AllDevisComponent;
  let fixture: ComponentFixture<AllDevisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllDevisComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllDevisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
