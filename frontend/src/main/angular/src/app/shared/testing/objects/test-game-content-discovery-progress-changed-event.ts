import {GameContentDiscoveryProgressChangedEvent} from "@backend";

export namespace TestGameContentDiscoveryProgressChangedEvent {

  export function twentyFivePercent(): GameContentDiscoveryProgressChangedEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 999,
      gamesDiscovered: 5,
      gameFilesDiscovered: 70
    };
  }
}
