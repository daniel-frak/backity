import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {ButtonComponent} from './button.component';
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";

describe('ButtonComponent', () => {
    let component: ButtonComponent;
    let fixture: ComponentFixture<ButtonComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ButtonComponent],
            providers: []
        })
            .compileComponents();

        fixture = TestBed.createComponent(ButtonComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy();
    });

    function getButtonDebugElement(): DebugElement {
        return fixture.debugElement.query(By.css('button'));
    }

    it('should disable the button when isLoading is true', () => {
        fixture.componentRef.setInput('isLoading', true);
        fixture.detectChanges();
        const buttonNativeElement = getButtonDebugElement().nativeElement;
        expect(buttonNativeElement.disabled).toBe(true);
    });

    it('should call actionAsync when provided', fakeAsync(() => {
        const actionAsyncSpy = vi.fn().mockResolvedValue(undefined);
        fixture.componentRef.setInput('actionAsync', actionAsyncSpy);
        fixture.detectChanges();

        const buttonDebugElement: DebugElement = getButtonDebugElement();
        buttonDebugElement.triggerEventHandler('click', null);
        tick();

        expect(actionAsyncSpy).toHaveBeenCalled();
        expect(component.isLoading()).toBe(false);
    }));

    it('should call action when provided', fakeAsync(() => {
        const actionSpy = vi.fn();
        fixture.componentRef.setInput('action', actionSpy);
        fixture.detectChanges();

        const buttonElement = getButtonDebugElement();
        buttonElement.triggerEventHandler('click', null);
        tick();

        expect(actionSpy).toHaveBeenCalled();
        expect(component.isLoading()).toBe(false);
    }));

    function getLoaderDebugElement() {
        return fixture.debugElement.query(By.css('.spinner-border'));
    }

    it('should show loader when isLoading is true', () => {
        fixture.componentRef.setInput('isLoading', true);
        fixture.detectChanges();

        const loaderElement: DebugElement = getLoaderDebugElement();
        expect(loaderElement).toBeTruthy();
    });

    it('should set isLoading to false after the action completes', fakeAsync(() => {
        fixture.componentRef.setInput('actionAsync', vi.fn().mockResolvedValue(undefined));

        component.onClick();
        tick();

        expect(component.isLoading()).toBe(false);
    }));

    it('should do nothing when onClick is called but isLoading is already true', fakeAsync(() => {
        fixture.detectChanges();

        let actionWasCalled = false;
        fixture.componentRef.setInput('isLoading', true);
        fixture.componentRef.setInput('actionAsync', vi.fn().mockImplementation(() => {
            return new Promise<void>((resolve) => {
                actionWasCalled = true;
                resolve();
            });
        }));

        component.onClick();
        tick();

        expect(actionWasCalled).toBe(false);
    }));

    it('should set isLoading to false when an error is thrown from action', async () => {
        const error = new Error('Test error');
        fixture.componentRef.setInput('action', () => {
            throw error;
        });
        await expect(component.onClick()).rejects.toThrow();
        expect(component.isLoading()).toBe(false);
    });

    it('should set correct size class', () => {
        fixture.componentRef.setInput('buttonSize', 'small');
        fixture.detectChanges();
        const buttonElement = fixture.debugElement.query(By.css('button'));
        expect(buttonElement.classes['btn-sm']).toBe(true);
    });

    it('should set outline style class', () => {
        fixture.componentRef.setInput('buttonStyle', 'primary');
        fixture.componentRef.setInput('outline', true);
        fixture.detectChanges();
        const buttonElement = fixture.debugElement.query(By.css('button'));
        expect(buttonElement.classes['btn-outline-primary']).toBe(true);
    });

    it('getSizeClass should return empty string if buttonSize is missing', () => {
        fixture.componentRef.setInput('buttonSize', undefined);
        expect(component.getSizeClass()).toBe('');
    });
});
