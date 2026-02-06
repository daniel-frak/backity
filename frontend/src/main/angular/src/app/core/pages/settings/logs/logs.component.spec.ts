import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LogsComponent} from './logs.component';
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessageService} from "@app/shared/backend/services/message.service";
import {Observable, of} from "rxjs";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('LogsComponent', () => {
  let component: LogsComponent;
  let fixture: ComponentFixture<LogsComponent>;
  let messageSimulator: MessageSimulator;
  let logsClient: SpyObj<LogsClient>;
  let messageService: SpyObj<MessageService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogsComponent],
      providers: [
        {
          provide: MessageService,
          useValue: createSpyObj(MessageService, ['watch'])
        },
        {
          provide: LogsClient,
          useValue: createSpyObj(LogsClient, ['getLogs'])
        }
      ]
    })
      .compileComponents();

    logsClient = TestBed.inject(LogsClient) as SpyObj<LogsClient>;
    logsClient.getLogs.and.returnValue(of([]) as Observable<any>);

    messageService = TestBed.inject(MessageService) as SpyObj<MessageService>;
    messageSimulator = MessageSimulator.given(messageService);
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
    expect(messageService.watch).toHaveBeenCalledWith(LogsMessageTopics.TopicLogs);
  });

  it('should add new logs to list', () => {
    const log: LogCreatedEvent = {
      maxLogs: 2,
      message: "Log3"
    };

    component.logs.set(['Log2', 'Log1']);
    messageSimulator.emit(LogsMessageTopics.TopicLogs, log);
    expect(component.logs()).toEqual(['Log3', 'Log2']);
  });

  it('should refresh logs', () => {
    logsClient.getLogs.and.returnValue(of(['Log2', 'Log3']) as Observable<any>);

    component.refresh();
    expect(component.logs()).toEqual(['Log3', 'Log2']);
    expect(component.logsAreLoading()).toBeFalse();
  })

  it('should refresh on init', () => {
    expect(logsClient.getLogs).toHaveBeenCalled();
  });

  it('should block refresh when already loading', () => {
    logsClient.getLogs.calls.reset();
    component.logsAreLoading.set(true);

    component.refresh();

    expect(logsClient.getLogs).not.toHaveBeenCalled();
  });
});
