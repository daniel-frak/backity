import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
    selector: 'app-named-value',
    imports: [],
    templateUrl: './named-value.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrl: './named-value.component.scss'
})
export class NamedValueComponent {

}
