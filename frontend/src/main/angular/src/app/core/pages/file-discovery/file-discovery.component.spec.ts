import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {
  FileDiscoveryInfoCardComponentStub
} from "@app/core/pages/file-discovery/file-discovery-info-card/file-discovery-info-card.component.stub";
import {
  DiscoveredFilesCardComponentStub
} from "@app/core/pages/file-discovery/discovered-files-card/discovered-files-card.component.stub";
import {
  FileDiscoveryInfoCardComponent
} from "@app/core/pages/file-discovery/file-discovery-info-card/file-discovery-info-card.component";
import {
  DiscoveredFilesCardComponent
} from "@app/core/pages/file-discovery/discovered-files-card/discovered-files-card.component";

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [
        FileDiscoveryComponent
      ]
    }).overrideComponent(FileDiscoveryComponent, {
      remove: {
        imports: [
          FileDiscoveryInfoCardComponent,
          DiscoveredFilesCardComponent
        ]
      },
      add: {
        imports: [
          FileDiscoveryInfoCardComponentStub,
          DiscoveredFilesCardComponentStub
        ]
      },
    })
      .compileComponents();

    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
