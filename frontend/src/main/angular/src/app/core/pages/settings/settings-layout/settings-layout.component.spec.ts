import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsLayoutComponent} from './settings-layout.component';
import {
  SettingsSideNavComponent
} from "@app/core/pages/settings/settings-layout/settings-side-nav/settings-side-nav.component";
import {RouterModule} from "@angular/router";

describe('SettingsLayoutComponent', () => {
  let component: SettingsLayoutComponent;
  let fixture: ComponentFixture<SettingsLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        SettingsLayoutComponent,
        SettingsSideNavComponent
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
