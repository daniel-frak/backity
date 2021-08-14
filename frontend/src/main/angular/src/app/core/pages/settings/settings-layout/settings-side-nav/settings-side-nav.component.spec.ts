import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsSideNavComponent } from './settings-side-nav.component';

describe('SettingsSideNavComponent', () => {
  let component: SettingsSideNavComponent;
  let fixture: ComponentFixture<SettingsSideNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsSideNavComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsSideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
