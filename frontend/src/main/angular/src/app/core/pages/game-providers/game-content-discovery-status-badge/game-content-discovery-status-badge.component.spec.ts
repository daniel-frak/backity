import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameContentDiscoveryStatusBadgeComponent} from './game-content-discovery-status-badge.component';

describe('DiscoveryStatusBadgeComponent', () => {
  let component: GameContentDiscoveryStatusBadgeComponent;
  let fixture: ComponentFixture<GameContentDiscoveryStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameContentDiscoveryStatusBadgeComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameContentDiscoveryStatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
