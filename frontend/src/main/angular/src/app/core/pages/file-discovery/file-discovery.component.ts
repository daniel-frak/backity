import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {
  DiscoveredFilesCardComponent
} from "@app/core/pages/file-discovery/discovered-files-card/discovered-files-card.component";
import {
  FileDiscoveryInfoCardComponent
} from "@app/core/pages/file-discovery/file-discovery-info-card/file-discovery-info-card.component";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, DiscoveredFilesCardComponent, FileDiscoveryInfoCardComponent]
})
export class FileDiscoveryComponent {

}
