import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NamedValueComponent } from './named-value.component';

describe('NamedValueComponent', () => {
  let component: NamedValueComponent;
  let fixture: ComponentFixture<NamedValueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NamedValueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NamedValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
