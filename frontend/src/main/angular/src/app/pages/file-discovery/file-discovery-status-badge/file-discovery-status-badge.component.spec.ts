import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileDiscoveryStatusBadgeComponent } from './file-discovery-status-badge.component';

describe('DiscoveryStatusBadgeComponent', () => {
  let component: FileDiscoveryStatusBadgeComponent;
  let fixture: ComponentFixture<FileDiscoveryStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileDiscoveryStatusBadgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDiscoveryStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
