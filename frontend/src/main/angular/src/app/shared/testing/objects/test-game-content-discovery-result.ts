import {GameContentDiscoveryOutcome, GameContentDiscoveryResult} from "@backend";

export namespace TestGameContentDiscoveryResult {

  export function successfulSubsequent(): GameContentDiscoveryResult {
    return {
      startedAt: "2022-04-29T15:00:00",
      stoppedAt: "2022-04-29T16:00:00",
      discoveryOutcome: GameContentDiscoveryOutcome.Success,
      lastSuccessfulDiscoveryCompletedAt: "2022-04-20T10:00:00",
      gamesDiscovered: 5,
      gameFilesDiscovered: 70
    };
  }
}
