import {GameFile} from "@backend";

export class TestGameFile {

  public static any(): GameFile {
    return {
      id: "someGameFileId",
      gameId: "someGameId",
      fileSource: {
        gameProviderId: "someGameProviderId",
        originalGameTitle: "Some game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      }
    };
  }
}
