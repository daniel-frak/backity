import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageSolutionStatusBadgeComponent } from './storage-solution-status-badge.component';
import {StorageSolutionStatus} from "@backend";

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

  it('should render storage solution status given not connected', () => {
    component.status = StorageSolutionStatus.NotConnected;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('NOT_CONNECTED');
    expect(fixture.nativeElement.querySelector('.badge')?.classList).toContain('bg-danger');
  })

  it('should render storage solution status given connected', () => {
    component.status = StorageSolutionStatus.Connected;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('CONNECTED');
    expect(fixture.nativeElement.querySelector('.badge')?.classList).toContain('bg-secondary');
  })

  it('should render default storage solution status given status is undefined', () => {
    component.status = undefined;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Connection status unknown');
    expect(fixture.nativeElement.querySelector('.badge')?.classList).toContain('bg-secondary');
  })
});
