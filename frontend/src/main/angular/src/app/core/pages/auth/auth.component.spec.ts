import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AuthComponent} from './auth.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule} from "@angular/forms";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {GogAuthComponent} from "@app/gog/pages/auth/gog-auth/gog-auth.component";

describe('AuthComponent', () => {
  let component: AuthComponent;
  let fixture: ComponentFixture<AuthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        AuthComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent,
        GogAuthComponent
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
