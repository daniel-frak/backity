import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPlaceholderComponent } from './loading-placeholder.component';

describe('LoadingPlaceholderComponent', () => {
  let component: LoadingPlaceholderComponent;
  let fixture: ComponentFixture<LoadingPlaceholderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingPlaceholderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingPlaceholderComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should handle duplicate widths', () => {
    component.widths = ['20rem', '20rem', '20rem'];
    fixture.detectChanges();
    const placeholders = fixture.nativeElement.querySelectorAll('.placeholder');
    expect(placeholders.length).toBe(3);
    expect(placeholders[0].style.width).toBe('20rem');
    expect(placeholders[1].style.width).toBe('20rem');
    expect(placeholders[2].style.width).toBe('20rem');
  });
});
