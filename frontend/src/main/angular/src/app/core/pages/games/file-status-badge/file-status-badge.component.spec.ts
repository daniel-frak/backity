import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileStatusBadgeComponent} from './file-status-badge.component';
import {FileBackupStatus} from "@backend";

describe('FileStatusBadgeComponent', () => {
  let component: FileStatusBadgeComponent;
  let fixture: ComponentFixture<FileStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileStatusBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have the default status as "Discovered"', () => {
    expect(component.status).toBe(FileBackupStatus.Discovered);
  });

  it('should return the correct badge class for "Discovered"', () => {
    component.status = FileBackupStatus.Discovered;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });

  it('should return the correct badge class for "Success"', () => {
    component.status = FileBackupStatus.Success;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-success');
  });

  it('should return the correct badge class for "In Progress"', () => {
    component.status = FileBackupStatus.InProgress;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-info');
  });

  it('should return the correct badge class for "Failed"', () => {
    component.status = FileBackupStatus.Failed;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-danger');
  });

  it('should return the correct badge class for "Enqueued"', () => {
    component.status = FileBackupStatus.Enqueued;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-warning');
  });

  it('should return the default badge class for undefined status', () => {
    component.status = undefined;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });
});
