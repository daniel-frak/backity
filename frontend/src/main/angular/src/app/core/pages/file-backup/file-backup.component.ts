import {Component} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {
  InProgressFilesCardComponent
} from "@app/core/pages/file-backup/in-progress-files-card/in-progress-files-card.component";
import {
  EnqueuedFileCopiesCardComponent
} from "@app/core/pages/file-backup/enqueued-file-copies-card/enqueued-file-copies-card.component";
import {
  ProcessedFileCopiesCardComponent
} from "@app/core/pages/file-backup/processed-files-card/processed-file-copies-card.component";

@Component({
  selector: 'app-downloads',
  templateUrl: './file-backup.component.html',
  styleUrls: ['./file-backup.component.scss'],
  standalone: true,
  imports: [
    PageHeaderComponent,
    InProgressFilesCardComponent,
    EnqueuedFileCopiesCardComponent,
    ProcessedFileCopiesCardComponent
  ]
})
export class FileBackupComponent {
}
