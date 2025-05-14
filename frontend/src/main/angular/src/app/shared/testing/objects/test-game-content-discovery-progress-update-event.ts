import {GameContentDiscoveryProgressUpdateEvent} from "@backend";

export class TestGameContentDiscoveryProgressUpdateEvent {

  public static twentyFivePercent(): GameContentDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };
  }
}
