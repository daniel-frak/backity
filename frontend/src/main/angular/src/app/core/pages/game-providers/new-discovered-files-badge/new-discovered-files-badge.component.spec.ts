import {ComponentFixture, TestBed} from '@angular/core/testing';

import {NewDiscoveredFilesBadgeComponent} from './new-discovered-files-badge.component';

describe('NewDiscoveredFilesBadgeComponent', () => {
  let component: NewDiscoveredFilesBadgeComponent;
  let fixture: ComponentFixture<NewDiscoveredFilesBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewDiscoveredFilesBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewDiscoveredFilesBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
