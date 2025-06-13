import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, ViewChild} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-icon-item',
  standalone: true,
  imports: [
    NgClass,
    NgIf
  ],
  templateUrl: './icon-item.component.html',
  styleUrl: './icon-item.component.scss'
})
export class IconItemComponent implements AfterViewInit {

  @Input() iconClass: string = '';
  @Input() size: 'lg' | 'md' | 'sm' = 'lg';

  @ViewChild('descriptorWrapper') descriptorWrapper?: ElementRef;
  showDescriptor = true;

  @ViewChild('buttonsWrapper') buttonsWrapper?: ElementRef;
  showButtons = true;

  @ViewChild('detailsWrapper') detailsWrapper?: ElementRef;
  showDetails = true;

  constructor(private readonly cdRef: ChangeDetectorRef) {
  }

  ngAfterViewInit() {
    this.showDescriptor = this.descriptorWrapper?.nativeElement.children.length > 0;
    this.showButtons = this.buttonsWrapper?.nativeElement.children.length > 0;
    this.showDetails = this.detailsWrapper?.nativeElement.children.length > 0;
    this.cdRef.detectChanges();
  }

  getIconSizeClass(): string {
    return `icon-holder-${this.size}`;
  }
}
