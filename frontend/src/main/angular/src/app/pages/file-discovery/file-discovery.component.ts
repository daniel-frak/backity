import { Component, OnInit } from '@angular/core';
import {DiscoveredFile, FileDiscoveryService, Pageable} from "../../../backend";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit {

  discoveredFiles?: DiscoveredFile[];

  constructor(private readonly fileDiscoveryService: FileDiscoveryService) { }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    const pageable: Pageable = {
      size: 100,
      page: 0
    };
    this.fileDiscoveryService.getDiscoveredFiles(pageable).subscribe(df => this.discoveredFiles = df.content);
  }

  discoverFiles() {
    this.fileDiscoveryService.discover().subscribe(() => console.info("Finished discovering"));
  }
}
