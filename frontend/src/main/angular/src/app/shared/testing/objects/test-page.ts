export class TestPage {

  public static of<T>(content: any[]): T {
    return {content: content} as T;
  }
}
