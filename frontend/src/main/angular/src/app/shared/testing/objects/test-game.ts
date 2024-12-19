import {GameFile} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";

export class TestGame {

  public static withFiles(files: GameFile[]) {
    return {
      id: "someGameId",
      title: "someGameTitle",
      files: files
    };
  }

  public static withDiscoveredFile() {
    return {
      id: "someGameId",
      title: "someGameTitle",
      files: [TestGameFile.discovered()]
    };
  }
}
