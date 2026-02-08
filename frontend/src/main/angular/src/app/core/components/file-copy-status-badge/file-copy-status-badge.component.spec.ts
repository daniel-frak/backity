import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileCopyStatusBadgeComponent} from './file-copy-status-badge.component';

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
  });

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should have the default status as undefined (Untracked)', () => {
    fixture.detectChanges();

    expect(component.status()).toBe(undefined);
  });
});
