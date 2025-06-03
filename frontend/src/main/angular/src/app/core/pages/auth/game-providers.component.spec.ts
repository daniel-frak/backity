import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameProvidersComponent} from './game-providers.component';
import {GogAuthComponentStub} from "@app/gog/pages/auth/gog-auth/gog-auth.component.stub";
import {GogAuthComponent} from "@app/gog/pages/auth/gog-auth/gog-auth.component";

describe('GameProvidersComponent', () => {
  let component: GameProvidersComponent;
  let fixture: ComponentFixture<GameProvidersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameProvidersComponent]
    })
      .overrideComponent(GameProvidersComponent, {
        remove: {imports: [GogAuthComponent]},
        add: {imports: [GogAuthComponentStub]}
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
