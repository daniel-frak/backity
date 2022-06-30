import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";
import {TableComponent} from "@app/shared/components/table/table.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import createSpyObj = jasmine.createSpyObj;

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;
  let messageServiceMock;

  beforeEach(async () => {
    messageServiceMock = createSpyObj(['onConnect']);

    await TestBed.configureTestingModule({
      declarations: [
        FileDiscoveryComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent,
        NewDiscoveredFilesBadgeComponent,
        TableComponent
      ],
      imports: [
        HttpClientTestingModule, NgbModule
      ],
      providers: [
        {
          provide: MessagesService,
          useValue: messageServiceMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
