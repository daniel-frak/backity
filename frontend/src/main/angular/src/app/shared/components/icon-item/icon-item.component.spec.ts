import {ComponentFixture, TestBed} from '@angular/core/testing';

import {IconItemComponent} from './icon-item.component';
import {Component} from "@angular/core";
import {By} from "@angular/platform-browser";

@Component({
  template: `
    <app-icon-item>
      <div title>My Title</div>
      <div descriptor>Some descriptor</div>
      <div hideableDetails>{{ hideableText }}</div>
      <div details>Always visible details</div>
    </app-icon-item>
  `
})
class TestHostComponent {

  hideableText = 'Hideable details';
}

describe('IconItemComponent', () => {
  let component: IconItemComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IconItemComponent],
      declarations: [TestHostComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.debugElement.query(By.directive(IconItemComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initially render the hideableDetails in the DOM', () => {
    expect(fixture.nativeElement.textContent).toContain(fixture.componentInstance.hideableText);
  });

  it('should toggle hideable details when toggleHideableDetailsAction is called', () => {
    component.showHideableDetails = true;

    component.toggleHideableDetailsAction();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain(fixture.componentInstance.hideableText);

    component.toggleHideableDetailsAction();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain(fixture.componentInstance.hideableText);
  });
});
