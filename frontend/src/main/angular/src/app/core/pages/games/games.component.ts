import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GamesWithFileCopiesCardComponent} from "@app/core/pages/games/games-with-files-card/games-with-file-copies-card.component";

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GamesWithFileCopiesCardComponent]
})
export class GamesComponent {

}
