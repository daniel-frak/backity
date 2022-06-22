import {Component, HostBinding, OnInit} from '@angular/core';

@Component({
  selector: 'app-settings-side-nav',
  templateUrl: './settings-side-nav.component.html',
  styleUrls: ['./settings-side-nav.component.scss'],
})
export class SettingsSideNavComponent implements OnInit {

  @HostBinding('class.minimized')
  minimizeSideNav: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
    if (this.isMobileClient()) {
      this.minimizeSideNav = true;
    }
  }

  private isMobileClient() {
    const ua = navigator.userAgent;
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini|Mobile|mobile|CriOS/i.test(ua);
  }
}
