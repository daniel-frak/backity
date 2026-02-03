import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Observable, Subscription} from "rxjs";
import {ComponentFixture} from "@angular/core/testing";
import SpyObj = jasmine.SpyObj;

export class MessageTesting {

  static mockWatchJson(messagesService: SpyObj<MessagesService>,
                       messageRouter: (destination: string, callback: ((message: any) => void)) => any): void {
    messagesService.watchJson.and.callFake((destination: string) => {
      return {
        subscribe: (callback: (message: any) => void) => {
          messageRouter(destination, callback);
          return Subscription.EMPTY;
        }
      } as Observable<any>;
    });
  }

  static async simulateWebSocketMessageReceived(
    fixture: ComponentFixture<any>, messagesService: SpyObj<MessagesService>, topic: string, message: any):
    Promise<void> {
    const topicCallback =
      MessageTesting.mockWatchJsonAndGetCallbackForTopic(messagesService, topic);

    fixture.detectChanges();
    await fixture.whenStable();

    topicCallback.execute(message);
    await fixture.whenStable();

    fixture.detectChanges();
  }

  static mockWatchJsonAndGetCallbackForTopic(messagesService: SpyObj<MessagesService>, topic: string) {
    const topicCallback = {
      execute: (message: any) => {
        console.error("Topic callback not found for: " + topic);
      }
    };

    MessageTesting.mockWatchJson(messagesService, (destination, callback) => {
      if (destination == topic) {
        topicCallback.execute = callback;
      }
    });

    return topicCallback;
  }
}
