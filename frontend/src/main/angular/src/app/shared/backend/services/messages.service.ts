import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from "rxjs";
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";
import {IMessage} from "@stomp/stompjs";

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  private readonly subscriptions: ReplaySubject<(client: RxStompService) => any> =
    new ReplaySubject<(client: RxStompService) => any>();

  constructor(private readonly rxStompService: RxStompService) {
    this.subscriptions.subscribe(func => func(rxStompService));
  }

  public watch(topic: string): Observable<IMessage> {
    return this.rxStompService.watch(topic);
  }
}
