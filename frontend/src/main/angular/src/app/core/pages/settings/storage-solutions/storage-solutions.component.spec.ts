import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StorageSolutionsComponent} from './storage-solutions.component';
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";

describe('StorageSolutionsComponent', () => {
  let component: StorageSolutionsComponent;
  let fixture: ComponentFixture<StorageSolutionsComponent>;

  class Page {

    get storageSolutions(): HTMLElement {
      return this.getElementByTestId('storage-solutions');
    }

    private getElementByTestId(testId: string): HTMLElement {
      const debugElement: DebugElement =
        fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
      return debugElement?.nativeElement;
    }
  }

  const page = new Page();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StorageSolutionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StorageSolutionsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show storage solutions given not loading', () => {
    component.storageSolutionsAreLoading.set(false);
    fixture.detectChanges();

    expect(page.storageSolutions).not.toBeUndefined();
  })

  it('should hide storage solutions given loading', () => {
    component.storageSolutionsAreLoading.set(true);
    fixture.detectChanges();

    expect(page.storageSolutions).toBeUndefined();
  })
});
