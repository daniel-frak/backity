import {messageCallbackType} from "@stomp/stompjs/src/types";
import {StompHeaders} from "@stomp/stompjs/src/stomp-headers";
import {Client} from "@stomp/stompjs";
import createSpyObj = jasmine.createSpyObj;

export class MessageTesting {

  static mockMessageService(messageRouter: (destination: string, callback: messageCallbackType) => any): any {
    const clientMock = createSpyObj('Client', ['subscribe']);
    clientMock.subscribe.and.callFake((destination: string, callback: messageCallbackType) => {
      messageRouter(destination, callback);

      return {
        id: "",
        unsubscribe(headers: StompHeaders | undefined): void {
        }
      };
    });

    const messagesServiceMock = createSpyObj('MessagesService', ['onConnect']);
    messagesServiceMock.onConnect.and.callFake((func: (client: Client) => any) => func(clientMock));

    return messagesServiceMock;
  }
}
