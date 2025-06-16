import {Component, HostBinding, OnInit} from '@angular/core';
import {NavigatorProviderService} from "@app/shared/services/navigator-provider.service";
import {NgClass} from '@angular/common';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
    selector: 'app-settings-side-nav',
    templateUrl: './settings-side-nav.component.html',
    styleUrls: ['./settings-side-nav.component.scss'],
    imports: [
        NgClass,
        RouterLinkActive,
        RouterLink,
    ]
})
export class SettingsSideNavComponent implements OnInit {

  @HostBinding('class.minimized')
  minimizeSideNav: boolean = false;

  constructor(private readonly navigatorProvider: NavigatorProviderService) {
  }

  ngOnInit(): void {
    if (this.isMobileClient()) {
      this.minimizeSideNav = true;
    }
  }

  private isMobileClient() {
    const ua = this.navigatorProvider.get().userAgent;
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini|Mobile|mobile|CriOS/i.test(ua);
  }
}
