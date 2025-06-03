import {ComponentFixture, TestBed} from '@angular/core/testing';

import {NewDiscoveredPopupComponent} from './new-discovered-popup.component';

describe('NewDiscoveredPopupComponent', () => {
  let component: NewDiscoveredPopupComponent;
  let fixture: ComponentFixture<NewDiscoveredPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewDiscoveredPopupComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewDiscoveredPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
