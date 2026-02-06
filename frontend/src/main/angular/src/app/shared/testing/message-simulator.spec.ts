import {MessageService} from "@app/shared/backend/services/message.service";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('MessageSimulator', () => {

  let messageServiceSpy: SpyObj<MessageService>;

  beforeEach(() => {
    messageServiceSpy = createSpyObj('MessageService', ['watch']);
  });

  it('should mock messageService.watch and route messages via given().emit()', () => {
    const topic = 'someTopic';
    const callback = jasmine.createSpy('callback');

    const messageSimulator = MessageSimulator.given(messageServiceSpy);

    messageServiceSpy.watch(topic).subscribe(callback);

    const message = {key: 'value'};
    messageSimulator.emit(topic, message);

    expect(callback).toHaveBeenCalledWith(message);
    expect(messageServiceSpy.watch).toHaveBeenCalledWith(topic);
  });
});
