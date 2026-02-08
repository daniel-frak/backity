import {SourceFile} from "@backend";

export namespace TestSourceFile {

  export function any(): SourceFile {
    return {
      id: "someSourceFileId",
      gameId: "someGameId",
      gameProviderId: "someGameProviderId",
      originalGameTitle: "Some game",
      originalFileName: "Some original file name",
      version: "Some version",
      url: "some.url",
      size: "3 GB",
      fileTitle: "currentGame.exe"
    };
  }
}
