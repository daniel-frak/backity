import {Page} from "@app/shared/components/table/page";

export namespace TestPage {

  export function of<T>(content: T[]): Page<T> {
    return {content: content} as Page<T>;
  }
}
