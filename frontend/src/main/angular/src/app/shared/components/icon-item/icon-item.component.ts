import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  HostBinding,
  input,
  signal,
  ViewChild
} from '@angular/core';
import {NgClass} from '@angular/common';
import {ButtonComponent} from '@app/shared/components/button/button.component';

@Component({
  selector: 'app-icon-item',
  standalone: true,
  imports: [NgClass, ButtonComponent],
  templateUrl: './icon-item.component.html',
  styleUrl: './icon-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class IconItemComponent implements AfterViewInit {

  readonly iconClass = input<string>('');
  readonly size = input<'lg' | 'md' | 'sm'>('lg');
  readonly showHideableDetailsOnInit = input<boolean>(true);

  @ViewChild('hideableDetailsWrapper', { read: ElementRef }) hideableDetailsWrapper?: ElementRef;
  @ViewChild('detailsWrapper') detailsWrapper?: ElementRef;

  readonly showHideableDetails = signal(true);

  hideableDetailsExist = false;
  detailsExist = true;

  // Toggle action
  readonly toggleHideableDetails = () => {
    this.showHideableDetails.update(v => !v);
  };

  @HostBinding('class')
  get hostClass(): string {
    return `icon-item-${this.size()}`;
  }

  ngAfterViewInit(): void {
    this.hideableDetailsExist = !!this.hideableDetailsWrapper?.nativeElement.children.length;
    this.detailsExist = this.detailsWrapper?.nativeElement.children.length > 1;

    this.showHideableDetails.set(this.hideableDetailsExist && this.showHideableDetailsOnInit());
  }

  getIconSizeClass(): string {
    return `icon-holder-${this.size()}`;
  }

  getDetailsSizeClass(): string {
    return `details-${this.size()}`;
  }
}
