import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ButtonComponent} from './button.component';
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function getButtonDebugElement(): DebugElement {
    return fixture.debugElement.query(By.css('button'));
  }

  it('should disable the button when isLoading is true', () => {
    component.isLoading = true;
    fixture.detectChanges();
    const buttonNativeElement = getButtonDebugElement().nativeElement;
    expect(buttonNativeElement.disabled).toBeTrue();
  });

  it('should call actionAsync when provided', fakeAsync(() => {
    const actionAsyncSpy = jasmine.createSpy().and.returnValue(Promise.resolve());
    component.actionAsync = actionAsyncSpy;
    fixture.detectChanges();

    const buttonDebugElement: DebugElement = getButtonDebugElement();
    buttonDebugElement.triggerEventHandler('click', null);
    tick();

    expect(actionAsyncSpy).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  }));

  it('should call action when provided', fakeAsync(() => {
    const actionSpy = jasmine.createSpy();
    component.action = actionSpy;
    fixture.detectChanges();

    const buttonElement = getButtonDebugElement();
    buttonElement.triggerEventHandler('click', null);
    tick();

    expect(actionSpy).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  }));

  function getLoaderDebugElement() {
    return fixture.debugElement.query(By.css('.spinner-border'));
  }

  it('should show loader when isLoading is true', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const loaderElement: DebugElement = getLoaderDebugElement();
    expect(loaderElement).toBeTruthy();
  });

  it('should set isLoading to false after the action completes', fakeAsync(() => {
    component.actionAsync = jasmine.createSpy().and.returnValue(Promise.resolve());

    component.onClick();
    tick();

    expect(component.isLoading).toBeFalse();
  }));

  it('should do nothing when onClick is called but isLoading is already true', fakeAsync(() => {
    let actionWasCalled = false;
    component.isLoading = true;
    component.actionAsync = jasmine.createSpy().and.callFake(() => {
      return new Promise<void>((resolve) => {
        actionWasCalled = true;
        resolve();
      });
    });

    component.onClick();
    tick();

    expect(actionWasCalled).toBeFalse();
  }));

  it('should print an error to the console', fakeAsync(() => {
    spyOn(console, 'error');
    let error = new Error('Test error');
    component.action = () => {
      throw error;
    };

    component.onClick();
    tick();

    expect(console.error).toHaveBeenCalledWith('Error during button action:', error);
  }));
});
