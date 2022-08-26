import { TestBed } from '@angular/core/testing';

import { ReturnsUtilsService } from './returns-utils.service';

describe('ReturnsUtilsService', () => {
  let service: ReturnsUtilsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReturnsUtilsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
