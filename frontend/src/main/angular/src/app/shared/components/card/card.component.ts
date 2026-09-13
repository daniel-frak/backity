import {ChangeDetectionStrategy, Component, input} from '@angular/core';


@Component({
    selector: 'app-card',
    imports: [],
    templateUrl: './card.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrl: './card.component.scss'
})
export class CardComponent {

  readonly cardTitle = input<string>();
}
