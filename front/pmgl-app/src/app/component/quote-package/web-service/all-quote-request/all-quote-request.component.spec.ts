import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllQuoteRequestComponent } from './all-quote-request.component';

describe('AllQuoteRequestComponent', () => {
  let component: AllQuoteRequestComponent;
  let fixture: ComponentFixture<AllQuoteRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllQuoteRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllQuoteRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
