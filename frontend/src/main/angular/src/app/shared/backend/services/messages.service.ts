import {Inject, Injectable} from '@angular/core';
import {ReplaySubject} from "rxjs";
import {Client} from "@stomp/stompjs";
import {STOMP_CLIENT} from "@app/shared/shared.module";

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  private readonly subscriptions: ReplaySubject<(client: Client) => any> =
    new ReplaySubject<(client: Client) => any>();

  constructor(@Inject(STOMP_CLIENT) private readonly stompClient: Client) {
    stompClient.onConnect = (frame) => {
      this.subscriptions.subscribe(func => func(stompClient));
    };
    stompClient.activate();
  }

  public onConnect(func: (client: Client) => any): void {
    this.subscriptions.next(client => func(client));
  }
}
