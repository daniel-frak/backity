import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileStatusBadgeComponent} from './file-status-badge.component';
import {FileCopyStatus} from "@backend";

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
    expect(component.status).toBe(FileCopyStatus.Discovered);
  });

  it('should return the correct badge class for "Discovered"', () => {
    component.status = FileCopyStatus.Discovered;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });

  it('should return the correct badge class for "Success"', () => {
    component.status = FileCopyStatus.Success;
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
    expect(component.getBadgeClass()).toBe('bg-info');
  });

  it('should return the default badge class for undefined status', () => {
    component.status = undefined;
    fixture.detectChanges();
    expect(component.getBadgeClass()).toBe('bg-secondary');
  });
});
