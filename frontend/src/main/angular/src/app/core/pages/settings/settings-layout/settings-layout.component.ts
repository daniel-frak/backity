import {Component, OnInit} from '@angular/core';
import {SettingsSideNavComponent} from './settings-side-nav/settings-side-nav.component';
import {RouterOutlet} from '@angular/router';

@Component({
    selector: 'app-settings-layout',
    templateUrl: './settings-layout.component.html',
    styleUrls: ['./settings-layout.component.scss'],
    imports: [SettingsSideNavComponent, RouterOutlet]
})
export class SettingsLayoutComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
