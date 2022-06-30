import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NavigatorProviderService {

  constructor() { }

  public get(): Navigator {
    return navigator;
  }
}
