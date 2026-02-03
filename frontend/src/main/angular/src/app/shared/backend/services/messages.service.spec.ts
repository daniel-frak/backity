import {TestBed} from '@angular/core/testing';

import {of} from "rxjs";
import {MessagesService} from './messages.service';
import {Client} from "@stomp/stompjs";
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";
import createSpyObj = jasmine.createSpyObj;

describe('MessagesService', () => {
  let service: MessagesService;
  let rxStompService: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: RxStompService,
          useValue: createSpyObj(Client, ['watch'])
        }
      ]
    });

    service = TestBed.inject(MessagesService);
    rxStompService = TestBed.inject(RxStompService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  it('should subscribe to functions on creation', () => {
    const funcSpy = jasmine.createSpy('funcSpy');
    service['subscriptions'].next(funcSpy);
    expect(funcSpy).toHaveBeenCalledWith(rxStompService);
  });

  it('should watch JSON WebSocket topics', () => {
    let calledDestination: string = "";
    let callbackResult: any = null;
    const expectedDestination = 'someDestination';
    const messagePayload = {test: 'data'};
    rxStompService.watch.and.callFake((destination: string) => {
      calledDestination = destination;
      return of({body: JSON.stringify(messagePayload)} as any);
    });

    service.watchJson<any>(expectedDestination)
      .subscribe(data => callbackResult = data);

    expect(calledDestination).toEqual(expectedDestination);
    expect(callbackResult).toEqual(messagePayload);
  });
});
