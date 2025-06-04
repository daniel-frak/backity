import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameFileVersionBadgeComponent } from './game-file-version-badge.component';

describe('GameFileVersionBadgeComponent', () => {
  let component: GameFileVersionBadgeComponent;
  let fixture: ComponentFixture<GameFileVersionBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameFileVersionBadgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GameFileVersionBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
