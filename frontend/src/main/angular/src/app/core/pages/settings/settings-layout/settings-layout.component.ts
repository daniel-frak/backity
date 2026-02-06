import {Component} from '@angular/core';
import {SettingsSideNavComponent} from './settings-side-nav/settings-side-nav.component';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-settings-layout',
  templateUrl: './settings-layout.component.html',
  styleUrl: './settings-layout.component.scss',
  imports: [SettingsSideNavComponent, RouterOutlet]
})
export class SettingsLayoutComponent {
}
