import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GogAuthComponent} from './gog-auth.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule} from "@angular/forms";

describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        GogAuthComponent,
        LoadedContentStubComponent
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GogAuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
