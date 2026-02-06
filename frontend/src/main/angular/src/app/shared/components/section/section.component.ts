import {Component, input} from '@angular/core';


@Component({
    selector: 'app-section',
    imports: [],
    templateUrl: './section.component.html',
    styleUrl: './section.component.scss'
})
export class SectionComponent {

  readonly sectionTitle = input<string>();
}
