import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SourceFileVersionBadgeComponent} from './source-file-version-badge.component';

describe('SourceFileVersionBadgeComponent', () => {
  let component: SourceFileVersionBadgeComponent;
  let fixture: ComponentFixture<SourceFileVersionBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SourceFileVersionBadgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SourceFileVersionBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
