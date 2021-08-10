import { Component, OnInit } from '@angular/core';
import {LogsClient} from "@backend";

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit {

  logs: string[] = [];
  public logsAreLoading: boolean = false;

  constructor(private readonly logsClient: LogsClient) { }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.logsAreLoading = true;
    this.logsClient.getLogs()
      .subscribe(logs => {
        this.logs = logs.reverse();
        this.logsAreLoading = false;
      });
  }
}
