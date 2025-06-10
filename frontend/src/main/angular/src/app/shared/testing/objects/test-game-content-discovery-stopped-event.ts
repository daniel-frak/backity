import {GameContentDiscoveryStoppedEvent} from "@backend";
import {TestGameContentDiscoveryResult} from "@app/shared/testing/objects/test-game-content-discovery-result";

export namespace TestGameContentDiscoveryStoppedEvent {

  export function successfulSubsequent(): GameContentDiscoveryStoppedEvent {
    return {
      gameProviderId: 'someGameProviderId',
      discoveryResult: TestGameContentDiscoveryResult.successfulSubsequent()
    };
  }
}
