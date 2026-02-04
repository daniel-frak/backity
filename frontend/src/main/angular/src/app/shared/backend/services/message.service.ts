import {Injectable} from '@angular/core';
import {map, Observable, ReplaySubject} from "rxjs";
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
      map(message => JSON.parse(message.body) as T)
    );
  }
}
