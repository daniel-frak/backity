import {TestBed} from '@angular/core/testing';

import {from, of} from "rxjs";
import {MessageService} from './message.service';
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";
import createSpyObj = jasmine.createSpyObj;

describe('MessageService', () => {
  let service: MessageService;
  let rxStompService: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: RxStompService,
          useValue: createSpyObj(RxStompService, ['watch'])
        }
      ]
    });

    service = TestBed.inject(MessageService);
    rxStompService = TestBed.inject(RxStompService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
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

    service.watch<any>(expectedDestination)
      .subscribe(data => callbackResult = data);

    expect(calledDestination).toEqual(expectedDestination);
    expect(callbackResult).toEqual(messagePayload);
  });

  it('should drop malformed JSON messages and continue the stream', () => {
    const expectedDestination = 'topic';
    const badBody = '{bad-json';
    const goodPayload = {ok: true};
    const values: any[] = [];
    let errored = false;

    spyOn(console, 'error');

    rxStompService.watch.and.callFake((destination: string) => {
      expect(destination).toEqual(expectedDestination);
      return from([
        {body: badBody} as any,
        {body: JSON.stringify(goodPayload)} as any
      ]);
    });

    service.watch<any>(expectedDestination).subscribe({
      next: v => values.push(v),
      error: () => {
        errored = true;
      }
    });

    expect(errored).toBeFalse();
    expect(values).toEqual([goodPayload]);
    expect(console.error).toHaveBeenCalled();
  });
});
