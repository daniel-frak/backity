import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileCopyStatusBadgeComponent} from './file-copy-status-badge.component';
import {FileCopyStatus} from "@backend";

describe('FileCopyStatusBadgeComponent', () => {
  let component: FileCopyStatusBadgeComponent;
  let fixture: ComponentFixture<FileCopyStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileCopyStatusBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileCopyStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have the default status as undefined (Untracked)', () => {
    expect(component.status).toBe(undefined);
  });

  it('should return the correct badge class for "Untracked"', () => {
    component.status = undefined;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });

  it('should return the correct badge class for "Tracked"', () => {
    component.status = FileCopyStatus.Tracked;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });

  it('should return the correct badge class for "Stored (integrity unknown)"', () => {
    component.status = FileCopyStatus.StoredIntegrityUnknown;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-success');
  });

  it('should return the correct badge class for "In Progress"', () => {
    component.status = FileCopyStatus.InProgress;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-warning');
  });

  it('should return the correct badge class for "Failed"', () => {
    component.status = FileCopyStatus.Failed;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-danger');
  });

  it('should return the correct badge class for "Enqueued"', () => {
    component.status = FileCopyStatus.Enqueued;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });

  it('should return the default badge class for undefined status', () => {
    component.status = undefined;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });
});
