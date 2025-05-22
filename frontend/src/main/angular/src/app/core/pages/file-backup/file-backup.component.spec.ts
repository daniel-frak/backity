import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FileBackupComponent} from './file-backup.component';
import {
  InProgressFilesCardComponentStub
} from "@app/core/pages/file-backup/in-progress-files-card/in-progress-files-card.component.stub";
import {
  EnqueuedFileCopiesCardComponentStub
} from "@app/core/pages/file-backup/enqueued-file-copies-card/enqueued-file-copies-card-component-stub.component";
import {
  ProcessedFilesCardComponentStub
} from "@app/core/pages/file-backup/processed-files-card/processed-files-card.component.stub";
import {
  EnqueuedFileCopiesCardComponent
} from "@app/core/pages/file-backup/enqueued-file-copies-card/enqueued-file-copies-card.component";
import {
  ProcessedFileCopiesCardComponent
} from "@app/core/pages/file-backup/processed-files-card/processed-file-copies-card.component";
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
            EnqueuedFileCopiesCardComponent,
            ProcessedFileCopiesCardComponent
          ]
        },
        add: {
          imports: [
            InProgressFilesCardComponentStub,
            EnqueuedFileCopiesCardComponentStub,
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
