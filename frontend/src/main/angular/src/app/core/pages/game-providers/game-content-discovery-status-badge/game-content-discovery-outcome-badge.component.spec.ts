import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameContentDiscoveryOutcomeBadgeComponent} from './game-content-discovery-outcome-badge.component';

describe('GameContentDiscoveryOutcomeBadgeComponent', () => {
  let component: GameContentDiscoveryOutcomeBadgeComponent;
  let fixture: ComponentFixture<GameContentDiscoveryOutcomeBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameContentDiscoveryOutcomeBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameContentDiscoveryOutcomeBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
