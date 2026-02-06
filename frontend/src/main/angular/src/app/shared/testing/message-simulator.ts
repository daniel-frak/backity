import {MessageService} from "@app/shared/backend/services/message.service";
import {Subject} from "rxjs";
import SpyObj = jasmine.SpyObj;

/**
 * Utility for simulating incoming {@link MessageService} messages.
 *
 * Use {@link given} to initialize it with a MessageService spy.
 * Use {@link emit} to push a message to a specific topic.
 */
export class MessageSimulator {

  private readonly subjects = new Map<string, Subject<any>>();

  private constructor(private readonly messageService: SpyObj<MessageService>) {
    messageService.watch.and.callFake((topic: string) => {
      return this.getOrCreateSubject(topic).asObservable();
    });
  }

  static given(messageService: SpyObj<MessageService>): MessageSimulator {
    return new MessageSimulator(messageService);
  }

  emit(topic: string, message: any): void {
    this.getOrCreateSubject(topic).next(message);
  }

  private getOrCreateSubject(topic: string): Subject<any> {
    let subject: Subject<any> | undefined = this.subjects.get(topic);

    if (!subject) {
      subject = new Subject<any>();
      this.subjects.set(topic, subject);
    }

    return subject;
  }
}
