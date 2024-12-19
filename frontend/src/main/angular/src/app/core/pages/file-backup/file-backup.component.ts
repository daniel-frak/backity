import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {
  InProgressFilesCardComponent
} from "@app/core/pages/file-backup/in-progress-files-card/in-progress-files-card.component";
import {
  EnqueuedFilesCardComponent
} from "@app/core/pages/file-backup/enqueued-files-card/enqueued-files-card.component";
import {
  ProcessedFilesCardComponent
} from "@app/core/pages/file-backup/processed-files-card/processed-files-card.component";

@Component({
  selector: 'app-downloads',
  templateUrl: './file-backup.component.html',
  styleUrls: ['./file-backup.component.scss'],
  standalone: true,
  imports: [
    PageHeaderComponent,
    InProgressFilesCardComponent,
    EnqueuedFilesCardComponent,
    ProcessedFilesCardComponent
  ]
})
export class FileBackupComponent {
}
