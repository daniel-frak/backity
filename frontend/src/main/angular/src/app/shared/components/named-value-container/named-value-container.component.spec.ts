import {ComponentFixture, TestBed} from '@angular/core/testing';

import {NamedValueContainerComponent} from './named-value-container.component';

describe('NamedValueContainerComponent', () => {
  let component: NamedValueContainerComponent;
  let fixture: ComponentFixture<NamedValueContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NamedValueContainerComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(NamedValueContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
