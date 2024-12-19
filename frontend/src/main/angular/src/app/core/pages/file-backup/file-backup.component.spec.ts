import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FileBackupComponent} from './file-backup.component';
import {
  InProgressFilesCardComponentStub
} from "@app/core/pages/file-backup/in-progress-files-card/in-progress-files-card.component.stub";
import {
  EnqueuedFilesCardComponentStub
} from "@app/core/pages/file-backup/enqueued-files-card/enqueued-files-card.component.stub";
import {
  ProcessedFilesCardComponentStub
} from "@app/core/pages/file-backup/processed-files-card/processed-files-card.component.stub";
import {
  EnqueuedFilesCardComponent
} from "@app/core/pages/file-backup/enqueued-files-card/enqueued-files-card.component";
import {
  ProcessedFilesCardComponent
} from "@app/core/pages/file-backup/processed-files-card/processed-files-card.component";
import {
  InProgressFilesCardComponent
} from "@app/core/pages/file-backup/in-progress-files-card/in-progress-files-card.component";

describe('FileBackupComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileBackupComponent]
    })
      .overrideComponent(FileBackupComponent, {
        remove: {
          imports: [
            InProgressFilesCardComponent,
            EnqueuedFilesCardComponent,
            ProcessedFilesCardComponent
          ]
        },
        add: {
          imports: [
            InProgressFilesCardComponentStub,
            EnqueuedFilesCardComponentStub,
            ProcessedFilesCardComponentStub
          ]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(FileBackupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
