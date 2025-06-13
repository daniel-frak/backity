import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, ViewChild} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {ButtonComponent} from "@app/shared/components/button/button.component";

@Component({
  selector: 'app-icon-item',
  standalone: true,
  imports: [
    NgClass,
    NgIf,
    ButtonComponent
  ],
  templateUrl: './icon-item.component.html',
  styleUrl: './icon-item.component.scss'
})
export class IconItemComponent implements AfterViewInit {

  readonly toggleHideableDetailsAction: () => void = () => this.toggleHideableDetails();

  @Input() iconClass: string = '';
  @Input() size: 'lg' | 'md' | 'sm' = 'lg';
  @Input() showHideableDetailsOnInit: boolean = true;

  @ViewChild('descriptorWrapper') descriptorWrapper?: ElementRef;
  descriptorExists = true;

  @ViewChild('buttonsWrapper') buttonsWrapper?: ElementRef;
  buttonsExist = true;

  @ViewChild('detailsWrapper') detailsWrapper?: ElementRef;
  detailsExist = true;

  @ViewChild('hideableDetailsWrapper', {read: ElementRef}) hideableDetailsWrapper?: ElementRef;
  hideableDetailsExist = true;

  showHideableDetails: boolean = true;

  constructor(private readonly cdRef: ChangeDetectorRef) {
  }

  ngAfterViewInit() {
    this.descriptorExists = this.descriptorWrapper?.nativeElement.children.length > 0;
    this.buttonsExist = this.buttonsWrapper?.nativeElement.children.length > 0;

    // 1 instead of 0, because hideableDetails is in there as a div:
    this.detailsExist = this.detailsWrapper?.nativeElement.children.length > 1;

    this.hideableDetailsExist = this.hideableDetailsWrapper?.nativeElement.children.length > 0;
    this.showHideableDetails = this.hideableDetailsExist && this.showHideableDetailsOnInit;

    this.cdRef.detectChanges();
  }

  getIconSizeClass(): string {
    return `icon-holder-${this.size}`;
  }

  toggleHideableDetails() {
    this.showHideableDetails = !this.showHideableDetails;
  }
}
