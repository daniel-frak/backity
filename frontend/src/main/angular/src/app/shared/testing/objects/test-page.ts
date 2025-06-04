export namespace TestPage {

  export function of<T>(content: any[]): T {
    return {content: content} as T;
  }
}
