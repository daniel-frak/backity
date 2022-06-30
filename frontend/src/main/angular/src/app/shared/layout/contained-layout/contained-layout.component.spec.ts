import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ContainedLayoutComponent} from './contained-layout.component';
import {RouterTestingModule} from "@angular/router/testing";

describe('ContainedLayoutComponent', () => {
  let component: ContainedLayoutComponent;
  let fixture: ComponentFixture<ContainedLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContainedLayoutComponent],
      imports: [RouterTestingModule]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContainedLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
