import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DefaultLayoutComponent} from './default-layout.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {NotificationContainerComponent} from "@app/notification-container/notification-container.component";
import {RouterModule} from "@angular/router";

describe('DefaultLayoutComponent', () => {
  let component: DefaultLayoutComponent;
  let fixture: ComponentFixture<DefaultLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        NgbModule,
        NotificationContainerComponent,
        DefaultLayoutComponent
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DefaultLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
