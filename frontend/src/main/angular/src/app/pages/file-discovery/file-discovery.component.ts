import { Component, OnInit } from '@angular/core';
import {DiscoveredFile, DiscoveredFileId, FileDiscoveryService, Pageable} from "../../../backend";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit {

  discoveredFiles?: DiscoveredFile[];
  public filesAreLoading: boolean = false;

  constructor(private readonly fileDiscoveryService: FileDiscoveryService) { }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.filesAreLoading = true;
    const pageable: Pageable = {
      size: 20,
      page: 0
    };
    this.fileDiscoveryService.getDiscoveredFiles(pageable).subscribe(df => {
      this.discoveredFiles = df.content;
      this.filesAreLoading = false;
    });
  }

  discoverFiles() {
    this.fileDiscoveryService.discover().subscribe(() => console.info("Finished discovering"));
  }

  enqueueFile(id?: DiscoveredFileId) {

  }
}
