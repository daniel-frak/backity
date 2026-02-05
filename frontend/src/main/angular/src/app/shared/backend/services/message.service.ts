import {Injectable} from '@angular/core';
import {filter, map, Observable, ReplaySubject} from "rxjs";
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private readonly subscriptions: ReplaySubject<(client: RxStompService) => any> =
    new ReplaySubject<(client: RxStompService) => any>();

  constructor(private readonly rxStompService: RxStompService) {
    this.subscriptions.subscribe(func => func(rxStompService));
  }

  public watch<T>(topic: string): Observable<T> {
    return this.rxStompService.watch(topic).pipe(
      map(message => {
        try {
          return JSON.parse(message.body) as T;
        } catch (e) {
          // Log and drop malformed messages, keep the stream alive
          console.error('Failed to parse WebSocket message', { topic, body: message.body, error: e });
          return null as unknown as T;
        }
      }),
      filter((val: T | null | undefined): val is T => val !== null && val !== undefined)
    );
  }
}
