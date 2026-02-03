import {Component, input} from '@angular/core';


@Component({
    selector: 'app-card',
    imports: [],
    templateUrl: './card.component.html',
    styleUrl: './card.component.scss'
})
export class CardComponent {

  readonly cardTitle = input<string>();
}
