import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LogsComponent} from './logs.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {Observable, of} from "rxjs";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('LogsComponent', () => {
  let component: LogsComponent;
  let fixture: ComponentFixture<LogsComponent>;
  let logSubscriptions: Function[];
  let logsClient: SpyObj<LogsClient>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        LogsComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent
      ],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watch'])
        },
        {
          provide: LogsClient,
          useValue: createSpyObj(LogsClient, ['getLogs'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    })
      .compileComponents();

    logsClient = TestBed.inject(LogsClient) as SpyObj<LogsClient>;
    logsClient.getLogs.and.returnValue(of([]) as Observable<any>);

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination == LogsMessageTopics.TopicLogs) {
        logSubscriptions.push(callback);
      }
    });

    logSubscriptions = [];
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
    logsClient.getLogs.and.returnValue(of(['Log2', 'Log3']) as Observable<any>);

    component.refresh();
    expect(component.logs).toEqual(['Log3', 'Log2']);
    expect(component.logsAreLoading).toBeFalse();
  })
});
