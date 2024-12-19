import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ContainedLayoutComponent} from './contained-layout.component';

describe('ContainedLayoutComponent', () => {
  let component: ContainedLayoutComponent;
  let fixture: ComponentFixture<ContainedLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContainedLayoutComponent]
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
