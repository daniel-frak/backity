import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import createSpyObj = jasmine.createSpyObj;
import {MessagesService} from "../../backend/services/messages.service";

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;
  let messageServiceMock;

  beforeEach(async () => {
    messageServiceMock = createSpyObj(['onConnect']);

    await TestBed.configureTestingModule({
      declarations: [
        FileDiscoveryComponent,
        LoadedContentStubComponent
      ],
      imports: [
        HttpClientTestingModule
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
