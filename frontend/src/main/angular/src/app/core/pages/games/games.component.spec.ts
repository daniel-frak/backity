import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GamesComponent} from './games.component';
import {GamesWithFileCopiesSectionComponent} from "@app/core/pages/games/games-with-files-section/games-with-file-copies-section.component";
import {
  GamesWithFileCopiesSectionComponentStub
} from "@app/core/pages/games/games-with-files-section/games-with-files-copies-section-component-stub.component";

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamesComponent]
    })
      .overrideComponent(GamesComponent, {
        remove: {imports: [GamesWithFileCopiesSectionComponent]},
        add: {imports: [GamesWithFileCopiesSectionComponentStub]}
      })
      .compileComponents();

    fixture = TestBed.createComponent(GamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
