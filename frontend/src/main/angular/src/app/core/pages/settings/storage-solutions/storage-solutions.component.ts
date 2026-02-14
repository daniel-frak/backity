import {Component, signal} from '@angular/core';
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {SectionComponent} from "@app/shared/components/section/section.component";

@Component({
  selector: 'app-storage-solutions',
  imports: [
    AutoLayoutComponent,
    LoadedContentComponent,
    SectionComponent
  ],
  templateUrl: './storage-solutions.component.html',
  styleUrl: './storage-solutions.component.scss',
})
export class StorageSolutionsComponent {

  storageSolutionsAreLoading = signal(false);
}
