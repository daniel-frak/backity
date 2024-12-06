import {Message} from "@stomp/stompjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {ComponentFixture, fakeAsync, tick} from "@angular/core/testing";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {Observable, Subscription} from "rxjs";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('MessageTesting', () => {
  let messagesServiceSpy: SpyObj<MessagesService>;

  beforeEach(() => {
    messagesServiceSpy = createSpyObj('MessagesService', ['watch']);
  });

  it('should mock watch and call messageRouter callback', () => {
    const messageRouter = jasmine.createSpy('messageRouter');
    MessageTesting.mockWatch(messagesServiceSpy, messageRouter);

    const callback = jasmine.createSpy('callback');
    const subscription = messagesServiceSpy.watch('someDestination').subscribe(callback);

    expect(messageRouter).toHaveBeenCalledWith('someDestination', jasmine.any(Function));
    expect(subscription).toBe(Subscription.EMPTY);
  });

  it('should simulate WebSocket message received', async () => {
    const fixture = jasmine.createSpyObj('fixture', ['detectChanges', 'whenStable']) as SpyObj<ComponentFixture<any>>;
    fixture.whenStable.and.resolveTo();

    const message = {key: 'value'};
    const topic = 'someTopic';
    let receivedMessage: Message | null = null;

    messagesServiceSpy.watch.and.callFake((destination: string) => {
      return {
        subscribe: (callback: (message: Message) => void) => {
          callback(message as any);
          return Subscription.EMPTY;
        }
      } as Observable<Message>;
    });
    messagesServiceSpy.watch(topic).subscribe(msg => {
      receivedMessage = msg;
    });

    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesServiceSpy, topic, message);

    expect(fixture.detectChanges).toHaveBeenCalledTimes(2);
    expect(fixture.whenStable).toHaveBeenCalledTimes(2);
    expect(messagesServiceSpy.watch).toHaveBeenCalledWith(topic);
    expect(receivedMessage).toEqual(message as any);
  });

  it('should return correct callback for topic', () => {
    let callbackRan: boolean = false;
    let receivedMessage: Message | null = null;
    const expectedMessage: Message = {body: JSON.stringify({key: 'value'})} as Message;
    const callbackForTopic =
      MessageTesting.mockWatchAndGetCallbackForTopic(messagesServiceSpy, 'someTopic');
    messagesServiceSpy.watch('someTopic').subscribe(message => {
      callbackRan = true;
      receivedMessage = message;
    });

    callbackForTopic.execute(expectedMessage);

    expect(callbackRan).toBe(true);
    expect(receivedMessage).toBe(expectedMessage as any);
  });

  it('should return default callback if topic not found', fakeAsync(() => {
    spyOn(console, 'error');
    let callbackRan: boolean = false;
    const expectedMessage: Message = {body: JSON.stringify({key: 'value'})} as Message;

    const callbackForTopic =
      MessageTesting.mockWatchAndGetCallbackForTopic(messagesServiceSpy, 'incorrectTopic');

    const subscription = messagesServiceSpy.watch('someTopic').subscribe(message => {
      callbackRan = true;
    });

    callbackForTopic.execute(expectedMessage);
    tick();

    subscription.unsubscribe();
    expect(callbackRan).toBe(false);
    expect(console.error).toHaveBeenCalledWith("Topic callback not found for: incorrectTopic");
  }));
});
