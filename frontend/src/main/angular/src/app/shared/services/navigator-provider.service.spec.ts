import {TestBed} from '@angular/core/testing';

import {NavigatorProviderService} from './navigator-provider.service';

describe('NavigatorProviderService', () => {
  let service: NavigatorProviderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NavigatorProviderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return navigator', () => {
    expect(service.get()).toBeTruthy();
  })
});
