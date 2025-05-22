import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameContentDiscoveryComponent} from './game-content-discovery.component';
import {
  GameContentDiscoveryInfoCardComponentStub
} from "@app/core/pages/file-discovery/game-content-discovery-info-card/game-content-discovery-info-card-component-stub.component";
import {
  DiscoveredFilesCardComponentStub
} from "@app/core/pages/file-discovery/discovered-file-copies-card/discovered-files-card.component.stub";
import {
  GameContentDiscoveryInfoCardComponent
} from "@app/core/pages/file-discovery/game-content-discovery-info-card/game-content-discovery-info-card.component";
import {
  DiscoveredFileCopiesCardComponent
} from "@app/core/pages/file-discovery/discovered-file-copies-card/discovered-file-copies-card.component";

describe('GameContentDiscoveryComponent', () => {
  let component: GameContentDiscoveryComponent;
  let fixture: ComponentFixture<GameContentDiscoveryComponent>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [
        GameContentDiscoveryComponent
      ]
    }).overrideComponent(GameContentDiscoveryComponent, {
      remove: {
        imports: [
          GameContentDiscoveryInfoCardComponent,
          DiscoveredFileCopiesCardComponent
        ]
      },
      add: {
        imports: [
          GameContentDiscoveryInfoCardComponentStub,
          DiscoveredFilesCardComponentStub
        ]
      },
    })
      .compileComponents();

    fixture = TestBed.createComponent(GameContentDiscoveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
