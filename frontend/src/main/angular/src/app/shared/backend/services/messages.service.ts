import {Injectable} from '@angular/core';
import {environment} from "@environment/environment";
import {ReplaySubject} from "rxjs";
import * as SockJS from 'sockjs-client';
import {Client, CompatClient} from "@stomp/stompjs";

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  private readonly stompClient?: Client;
  private readonly subscriptions: ReplaySubject<(client: Client) => any> =
    new ReplaySubject<(client: Client) => any>();

  /*
  https://grokonez.com/angular-12-springboot-websocket

  npm install @stomp/stompjs
  npm install sockjs-client
  npm install @types/sockjs-client
   */

  constructor() {
    if (environment.mockMessages) {
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(environment.apiUrl + '/messages')
    });

    const _this = this;
    this.stompClient.onConnect = function (frame) {
      // All subscribes must be done is this callback
      // This is needed because this will be executed after a (re)connect
      console.log("Executing stompClient subscriptions...");
      _this.subscriptions.subscribe(func => func(_this.stompClient as CompatClient));
    };
    this.stompClient.activate();
  }

  public onConnect(func: (client: Client) => any): void {
    this.subscriptions.next(client => func(client));
  }
}
