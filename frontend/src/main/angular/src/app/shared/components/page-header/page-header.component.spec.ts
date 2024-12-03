import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PageHeaderComponent} from './page-header.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

describe('PageHeaderComponent', () => {
  let component: PageHeaderComponent;
  let fixture: ComponentFixture<PageHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [NgbModule, PageHeaderComponent]
})
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PageHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
