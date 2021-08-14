import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-settings-side-nav',
  templateUrl: './settings-side-nav.component.html',
  styleUrls: ['./settings-side-nav.component.scss']
})
export class SettingsSideNavComponent implements OnInit {

  @Input()
  hideSideNav: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
