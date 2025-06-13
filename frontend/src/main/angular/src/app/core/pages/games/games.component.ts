import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GamesWithFileCopiesSectionComponent} from "@app/core/pages/games/games-with-files-section/games-with-file-copies-section.component";

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GamesWithFileCopiesSectionComponent]
})
export class GamesComponent {

}
