import { TestBed } from '@angular/core/testing';

import { MessagesService } from './messages.service';
import {environment} from "@environment/environment";

describe('MessagesService', () => {
  let service: MessagesService;

  beforeEach(() => {
    environment.mockMessages = true;
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
