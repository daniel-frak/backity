import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {
  DiscoveredFilesCardComponent
} from "@app/core/pages/file-discovery/discovered-files-card/discovered-files-card.component";
import {
  GameContentDiscoveryInfoCardComponent
} from "@app/core/pages/file-discovery/game-content-discovery-info-card/game-content-discovery-info-card.component";

@Component({
  selector: 'app-game-content-discovery',
  templateUrl: './game-content-discovery.component.html',
  styleUrls: ['./game-content-discovery.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, DiscoveredFilesCardComponent, GameContentDiscoveryInfoCardComponent]
})
export class GameContentDiscoveryComponent {

}
