import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsSideNavComponent} from './settings-side-nav.component';
import {NavigatorProviderService} from "@app/shared/services/navigator-provider.service";
import {RouterModule} from "@angular/router";

describe('SettingsSideNavComponent', () => {
  let component: SettingsSideNavComponent;
  let fixture: ComponentFixture<SettingsSideNavComponent>;
  const navigatorMock = {
    userAgent: ''
  };

  beforeEach(async () => {
    navigatorMock.userAgent = 'Desktop';
    const navigatorProviderServiceMock: NavigatorProviderService = {
      get: () => navigatorMock as any
    };
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        SettingsSideNavComponent
      ],
      providers: [
        {
          provide: NavigatorProviderService,
          useValue: navigatorProviderServiceMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsSideNavComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set minimizeSideNav to false if desktop client', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should set minimizeSideNav to true if mobile client', () => {
    navigatorMock.userAgent = 'Android';
    fixture.detectChanges();

    expect(component.minimizeSideNav).toBeTrue();
  });
});
