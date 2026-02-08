import {GameFile} from "@backend";

export namespace TestGameFile {

  export function any(): GameFile {
    return {
      id: "someGameFileId",
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
