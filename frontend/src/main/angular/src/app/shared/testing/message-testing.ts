import {Message} from "@stomp/stompjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Observable, Subscription} from "rxjs";
import {ComponentFixture} from "@angular/core/testing";
import SpyObj = jasmine.SpyObj;

export class MessageTesting {

  static mockWatch(messagesService: SpyObj<MessagesService>,
                   messageRouter: (destination: string, callback: ((message: Message) => void)) => any): void {
    messagesService.watch.and.callFake((destination: string) => {
      return {
        subscribe: (callback: (message: Message) => void) => {
          messageRouter(destination, callback);
          return Subscription.EMPTY;
        }
      } as Observable<Message>;
    });
  }

  static async simulateWebSocketMessageReceived(
    fixture: ComponentFixture<any>, messagesService: SpyObj<MessagesService>, topic: string, message: any):
    Promise<void> {
    const payload: Message = {body: JSON.stringify(message)} as any;
    const topicCallback =
      MessageTesting.mockWatchAndGetCallbackForTopic(messagesService, topic);

    fixture.detectChanges();
    await fixture.whenStable();

    topicCallback.execute(payload);
    await fixture.whenStable();

    fixture.detectChanges();
  }

  static mockWatchAndGetCallbackForTopic(messagesService: SpyObj<MessagesService>, topic: string) {
    let topicCallback = {
      execute: (message: Message) => {
        console.error("Topic callback not found for: " + topic);
      }
    };

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination == topic) {
        topicCallback.execute = callback;
      }
    });

    return topicCallback;
  }
}
