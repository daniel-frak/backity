import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LogsComponent} from './logs.component';
import { provideHttpClientTesting } from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {MessageTesting} from "@app/shared/testing/message-testing";
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import createSpyObj = jasmine.createSpyObj;

describe('LogsComponent', () => {
  let component: LogsComponent;
  let fixture: ComponentFixture<LogsComponent>;
  let logSubscriptions: Function[];
  let logsClientMock: any;

  beforeEach(async () => {
    logSubscriptions = [];

    const messagesServiceMock = MessageTesting.mockMessageService(
      (destination, callback) => {
        if (destination == LogsMessageTopics.TopicLogs) {
          logSubscriptions.push(callback);
        }
      });

    logsClientMock = createSpyObj(LogsClient, ['getLogs']);
    logsClientMock.getLogs.and.returnValue({subscribe: (s: (f: any) => any) => s([])});

    await TestBed.configureTestingModule({
    declarations: [
        LogsComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent
    ],
    imports: [],
    providers: [
        {
            provide: MessagesService,
            useValue: messagesServiceMock
        },
        {
            provide: LogsClient,
            useValue: logsClientMock
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
    ]
})
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to new logs', () => {
    expect(logSubscriptions.length).toBe(1);
  });

  it('should add new logs to list', () => {
    const log: LogCreatedEvent = {
      maxLogs: 2,
      message: "Log3"
    };

    component.logs = ['Log2', 'Log1'];
    logSubscriptions[0]({body: JSON.stringify(log)});
    expect(component.logs).toEqual(['Log3', 'Log2']);
  });

  it('should refresh logs', () => {
    component.logs = ['Log2', 'Log1'];
    logsClientMock.getLogs.and.returnValue({subscribe: (s: (f: any) => any) => s(['Log2', 'Log3'])});

    component.refresh();
    expect(component.logs).toEqual(['Log3', 'Log2']);
    expect(component.logsAreLoading).toBeFalse();
  })
});
