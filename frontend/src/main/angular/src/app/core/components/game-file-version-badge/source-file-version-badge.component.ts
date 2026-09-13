import {ChangeDetectionStrategy, Component, input} from '@angular/core';

@Component({
    selector: 'app-game-file-version-badge',
    imports: [],
    templateUrl: './source-file-version-badge.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrl: './source-file-version-badge.component.scss'
})
export class SourceFileVersionBadgeComponent {

  readonly version = input<string>('');
}
