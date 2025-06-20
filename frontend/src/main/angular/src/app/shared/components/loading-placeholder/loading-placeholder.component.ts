import { Component, Input } from '@angular/core';
import { NgStyle } from "@angular/common";

@Component({
    selector: 'app-loading-placeholder',
    imports: [
    NgStyle
],
    templateUrl: './loading-placeholder.component.html',
    styleUrl: './loading-placeholder.component.scss'
})
export class LoadingPlaceholderComponent {

  @Input() widths: string[] = ['6rem'];
}
