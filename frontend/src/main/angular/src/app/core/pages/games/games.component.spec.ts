import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GamesComponent} from './games.component';
import {GamesWithFilesCardComponent} from "@app/core/pages/games/games-with-files-card/games-with-files-card.component";
import {
  GamesWithFilesCardComponentStub
} from "@app/core/pages/games/games-with-files-card/games-with-files-card.component.stub";

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamesComponent]
    })
      .overrideComponent(GamesComponent, {
        remove: {imports: [GamesWithFilesCardComponent]},
        add: {imports: [GamesWithFilesCardComponentStub]}
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
