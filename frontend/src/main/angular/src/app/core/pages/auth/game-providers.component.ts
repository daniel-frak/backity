import {Component, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GogAuthComponent} from '@app/gog/pages/auth/gog-auth/gog-auth.component';

@Component({
  selector: 'app-game-providers',
  templateUrl: './game-providers.component.html',
  styleUrls: ['./game-providers.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GogAuthComponent]
})
export class GameProvidersComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
