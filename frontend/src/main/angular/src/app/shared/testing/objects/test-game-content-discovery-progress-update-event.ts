import {GameContentDiscoveryProgressUpdateEvent} from "@backend";

export namespace TestGameContentDiscoveryProgressUpdateEvent {

  export function twentyFivePercent(): GameContentDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };
  }

  export function oneHundredPercent(): GameContentDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 100,
      timeLeftSeconds: 0
    };
  }
}
