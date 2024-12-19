import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GamesWithFilesCardComponent} from "@app/core/pages/games/games-with-files-card/games-with-files-card.component";

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GamesWithFilesCardComponent]
})
export class GamesComponent {

}
