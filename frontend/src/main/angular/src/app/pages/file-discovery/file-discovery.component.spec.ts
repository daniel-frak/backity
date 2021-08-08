import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "../../shared/components/loaded-content/loaded-content.component.stub";

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        FileDiscoveryComponent,
        LoadedContentStubComponent
      ],
      imports: [
        HttpClientTestingModule
      ]
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
