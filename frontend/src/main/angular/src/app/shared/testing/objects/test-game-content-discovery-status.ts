import {GameContentDiscoveryStatus} from "@backend";

export namespace TestGameContentDiscoveryStatus {

  export function inProgress(): GameContentDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: true
    };
  }

  export function notInProgress(): GameContentDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: false
    };
  }
}
