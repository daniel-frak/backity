import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-theme',
    templateUrl: './theme.component.html',
    styleUrl: './theme.component.scss',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [FormsModule]
})
export class ThemeComponent {
}
