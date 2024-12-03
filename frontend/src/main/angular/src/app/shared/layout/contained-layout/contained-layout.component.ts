import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-contained-layout',
  templateUrl: './contained-layout.component.html',
  styleUrls: ['./contained-layout.component.scss'],
  standalone: true,
  imports: [RouterOutlet]
})
export class ContainedLayoutComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
