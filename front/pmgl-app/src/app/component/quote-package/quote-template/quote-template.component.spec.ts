import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuoteTemplateComponent } from './quote-template.component';

describe('QuoteTemplateComponent', () => {
  let component: QuoteTemplateComponent;
  let fixture: ComponentFixture<QuoteTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [QuoteTemplateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuoteTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
