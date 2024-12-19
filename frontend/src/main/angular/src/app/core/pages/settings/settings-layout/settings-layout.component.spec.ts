import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SettingsLayoutComponent} from './settings-layout.component';
import {provideRouter} from "@angular/router";

describe('SettingsLayoutComponent', () => {
  let component: SettingsLayoutComponent;
  let fixture: ComponentFixture<SettingsLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsLayoutComponent],
      providers: [provideRouter([])]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
