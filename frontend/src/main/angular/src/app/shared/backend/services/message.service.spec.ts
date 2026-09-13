import {TestBed} from '@angular/core/testing';

import {from, of} from "rxjs";
import {MessageService} from './message.service';
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";
import {Mocked} from "vitest";

describe('MessageService', () => {
    let service: MessageService;
    let rxStompService: Mocked<RxStompService>;

    beforeEach(() => {
        rxStompService = {
            watch: vi.fn()
        } as unknown as Mocked<RxStompService>;
        TestBed.configureTestingModule({
            providers: [
                {
                    provide: RxStompService,
                    useValue: rxStompService
                }
            ]
        });
        service = TestBed.inject(MessageService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should watch JSON WebSocket topics', () => {
        let calledDestination: string = "";
        let callbackResult: any = null;
        const expectedDestination = 'someDestination';
        const messagePayload = { test: 'data' };
        rxStompService.watch.mockImplementation((destination: string) => {
            calledDestination = destination;
            return of({ body: JSON.stringify(messagePayload) } as any);
        });

        service.watch<any>(expectedDestination)
            .subscribe(data => callbackResult = data);

        expect(calledDestination).toEqual(expectedDestination);
        expect(callbackResult).toEqual(messagePayload);
    });

    it('should drop malformed JSON messages and continue the stream', () => {
        const expectedDestination = 'topic';
        const badBody = '{bad-json';
        const goodPayload = { ok: true };
        const values: any[] = [];
        let errored = false;

        vi.spyOn(console, 'error').mockReturnValue(undefined);

        rxStompService.watch.mockImplementation((destination: string) => {
            expect(destination).toEqual(expectedDestination);
            return from([
                { body: badBody } as any,
                { body: JSON.stringify(goodPayload) } as any
            ]);
        });

        service.watch<any>(expectedDestination).subscribe({
            next: v => values.push(v),
            error: () => {
                errored = true;
            }
        });

        expect(errored).toBe(false);
        expect(values).toEqual([goodPayload]);
        expect(console.error).toHaveBeenCalled();
    });
});
