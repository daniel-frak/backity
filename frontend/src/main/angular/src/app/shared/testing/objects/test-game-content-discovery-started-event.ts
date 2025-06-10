import {GameContentDiscoveryStartedEvent} from "@backend";

export namespace TestGameContentDiscoveryStartedEvent {

  export function any(): GameContentDiscoveryStartedEvent {
    return {
      gameProviderId: 'someGameProviderId'
    };
  }
}
