import {TestBed} from '@angular/core/testing';

import {MessagesService} from './messages.service';
import {STOMP_CLIENT} from "@app/shared/shared.module";
import {Client} from "@stomp/stompjs";
import {messageCallbackType} from "@stomp/stompjs/src/types";
import createSpyObj = jasmine.createSpyObj;

describe('MessagesService', () => {
  let service: MessagesService;
  let stompClientMock: any;

  beforeEach(() => {
    stompClientMock = createSpyObj(Client, ['onConnect', 'activate', 'subscribe']);

    TestBed.configureTestingModule({
      providers: [
        {
          provide: STOMP_CLIENT,
          useValue: stompClientMock
        }
      ]
    });
    service = TestBed.inject(MessagesService);
    stompClientMock.onConnect()
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should activate on init', () => {
    expect(stompClientMock.activate).toHaveBeenCalled();
  });

  it('should subscribe to messages onConnect', () => {
    let calledDestination: string = "";
    let callbackRan: boolean = false;

    stompClientMock.subscribe.and.callFake((destination: string, callback: messageCallbackType) => {
      calledDestination = destination;
      callback(null as any);
    });

    service.onConnect(client => client.subscribe("someDestination", () => callbackRan = true));

    expect(calledDestination).toEqual("someDestination");
    expect(callbackRan).toBeTrue();
  });
});
