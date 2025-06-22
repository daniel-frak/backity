import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectComponent } from './select.component';

describe('SelectComponent', () => {
  let component: SelectComponent<string>;
  let fixture: ComponentFixture<SelectComponent<string>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectComponent<string>);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all provided elements as options', () => {
    component.elements = ['One', 'Two', 'Three'];
    component.label = 'Select an option';
    fixture.detectChanges();

    const options = fixture.nativeElement.querySelectorAll('option');
    expect(options.length).toBe(3);
    expect(options[0].textContent).toContain('One');
    expect(options[1].textContent).toContain('Two');
    expect(options[2].textContent).toContain('Three');
  });

  it('should set label correctly', () => {
    component.label = 'Custom Label';
    fixture.detectChanges();

    const label = fixture.nativeElement.querySelector('label');
    expect(label.textContent).toContain('Custom Label');
  });

  it('should apply form-floating class when "floating" is true', () => {
    component.floating = true;
    fixture.detectChanges();

    const wrapper = fixture.nativeElement.querySelector('div');
    expect(wrapper.classList).toContain('form-floating');
  });

  it('should not apply form-floating class when "floating" is false', () => {
    component.floating = false;
    fixture.detectChanges();

    const wrapper = fixture.nativeElement.querySelector('div');
    expect(wrapper.classList).not.toContain('form-floating');
  });

  it('should propagate value changes via handleChange()', () => {
    const spy = spyOn(component as any, 'onChange');
    const testValue = 'Two';

    component.handleChange(testValue);
    expect(component.value).toBe(testValue);
    expect(spy).toHaveBeenCalledWith(testValue);
  });

  it('should call registered onTouched and onChange from ControlValueAccessor', () => {
    let changed = '';
    let touched = false;

    component.registerOnChange((value: string) => (changed = value));
    component.registerOnTouched(() => (touched = true));

    const value = 'Selected';
    component.handleChange(value);

    expect(changed).toBe(value);
    expect(touched).toBeTrue();
  });
});
