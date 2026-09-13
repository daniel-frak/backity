import {MessageSimulator} from "@app/shared/testing/message-simulator";
import {Mocked} from "vitest";
import {MessageService} from "@app/shared/backend/services/message.service";

describe('MessageSimulator', () => {

    let messageServiceSpy: Mocked<MessageService>;

    beforeEach(() => {
        messageServiceSpy = {
            watch: vi.fn()
        } as unknown as Mocked<MessageService>;
    });

    it('should mock messageService.watch and route messages via given().emit()', () => {
        const topic = 'someTopic';
        const callback = vi.fn().mockName('callback');

        const messageSimulator = MessageSimulator.given(messageServiceSpy);

        messageServiceSpy.watch(topic).subscribe(callback);

        const message = { key: 'value' };
        messageSimulator.emit(topic, message);

        expect(callback).toHaveBeenCalledWith(message);
        expect(messageServiceSpy.watch).toHaveBeenCalledWith(topic);
    });
});
