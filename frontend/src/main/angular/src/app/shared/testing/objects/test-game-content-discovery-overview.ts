import {GameContentDiscoveryOverview} from "@backend";
import {TestGameContentDiscoveryResult} from "@app/shared/testing/objects/test-game-content-discovery-result";

export namespace TestGameContentDiscoveryOverview {

  const TEST_GAME_PROVIDER_ID = 'someGameProviderId';

  export function inProgress(): GameContentDiscoveryOverview {
    return {
      gameProviderId: TEST_GAME_PROVIDER_ID,
      isInProgress: true
    };
  }

  export function notInProgressAfterSuccessfulSubsequent(): GameContentDiscoveryOverview {
    return {
      gameProviderId: TEST_GAME_PROVIDER_ID,
      isInProgress: false,
      progress: undefined,
      lastDiscoveryResult: TestGameContentDiscoveryResult.successfulSubsequent()
    };
  }

  export function inProgressAtTwentyFivePercent(): GameContentDiscoveryOverview {
    return {
      gameProviderId: TEST_GAME_PROVIDER_ID,
      isInProgress: true,
      progress: {
        percentage: 25,
        timeLeftSeconds: 999,
        gamesDiscovered: 5,
        gameFilesDiscovered: 70
      },
    };
  }

  export function notInProgress(): GameContentDiscoveryOverview {
    return {
      gameProviderId: TEST_GAME_PROVIDER_ID,
      isInProgress: false
    };
  }
}
