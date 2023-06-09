import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileStatusBadgeComponent} from './file-status-badge.component';

describe('FileStatusBadgeComponent', () => {
  let component: FileStatusBadgeComponent;
  let fixture: ComponentFixture<FileStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileStatusBadgeComponent ]
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
});
