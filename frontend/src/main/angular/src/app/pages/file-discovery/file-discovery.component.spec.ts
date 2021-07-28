import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileDiscoveryComponent } from './file-discovery.component';

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FileDiscoveryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
