import {MessageService} from "@app/shared/backend/services/message.service";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('MessageSimulator', () => {

  let messagesServiceSpy: SpyObj<MessageService>;

  beforeEach(() => {
    messagesServiceSpy = createSpyObj('MessagesService', ['watch']);
  });

  it('should mock messageService.watch and route messages via given().emit()', () => {
    const topic = 'someTopic';
    const callback = jasmine.createSpy('callback');

    const messageSimulator = MessageSimulator.given(messagesServiceSpy);

    messagesServiceSpy.watch(topic).subscribe(callback);

    const message = {key: 'value'};
    messageSimulator.emit(topic, message);

    expect(callback).toHaveBeenCalledWith(message);
    expect(messagesServiceSpy.watch).toHaveBeenCalledWith(topic);
  });
});
