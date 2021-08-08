import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AuthComponent} from './auth.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "../../shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule} from "@angular/forms";

describe('AuthComponent', () => {
  let component: AuthComponent;
  let fixture: ComponentFixture<AuthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AuthComponent,
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
    fixture = TestBed.createComponent(AuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
