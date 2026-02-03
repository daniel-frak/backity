import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LogsComponent} from './logs.component';
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Observable, of, Subject} from "rxjs";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('LogsComponent', () => {
  let component: LogsComponent;
  let fixture: ComponentFixture<LogsComponent>;
  let logsSubject: Subject<LogCreatedEvent>;
  let logsClient: SpyObj<LogsClient>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    logsSubject = new Subject();

    await TestBed.configureTestingModule({
      imports: [LogsComponent],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watchJson'])
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

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    messagesService.watchJson.and.callFake(((destination: string) => {
      if (destination == LogsMessageTopics.TopicLogs) {
        return logsSubject;
      }
      return of();
    }) as any);
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
    expect(messagesService.watchJson).toHaveBeenCalledWith(LogsMessageTopics.TopicLogs);
  });

  it('should add new logs to list', () => {
    const log: LogCreatedEvent = {
      maxLogs: 2,
      message: "Log3"
    };

    component.logs.set(['Log2', 'Log1']);
    logsSubject.next(log);
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
});
