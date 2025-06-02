import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SectionComponent} from './section.component';

describe('SectionComponent', () => {
  let component: SectionComponent;
  let fixture: ComponentFixture<SectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectionComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render title when provided', () => {
    component.sectionTitle = 'Test Title';

    fixture.detectChanges();

    const nativeElement = fixture.nativeElement;
    const h2Element = nativeElement.querySelector('h2');
    expect(h2Element).toBeTruthy();
    expect(h2Element.textContent).toContain('Test Title');
  });

  it('should not render title when not provided', () => {
    component.sectionTitle = undefined;

    fixture.detectChanges();

    const nativeElement = fixture.nativeElement;
    expect(nativeElement.querySelector('h2')).toBeNull();
  });
});
