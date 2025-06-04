import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageSolutionStatusBadgeComponent } from './storage-solution-status-badge.component';

describe('StorageSolutionStatusBadgeComponent', () => {
  let component: StorageSolutionStatusBadgeComponent;
  let fixture: ComponentFixture<StorageSolutionStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StorageSolutionStatusBadgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StorageSolutionStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
