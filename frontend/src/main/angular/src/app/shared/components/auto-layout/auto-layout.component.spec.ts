import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AutoLayoutComponent} from './auto-layout.component';

describe('AutoLayoutComponent', () => {
  let component: AutoLayoutComponent;
  let fixture: ComponentFixture<AutoLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutoLayoutComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AutoLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
