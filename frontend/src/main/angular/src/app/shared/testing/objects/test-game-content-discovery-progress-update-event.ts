import {GameContentDiscoveryProgressUpdateEvent} from "@backend";

export class TestGameContentDiscoveryProgressUpdateEvent {

  public static twentyFivePercent(): GameContentDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };
  }

  public static oneHundredPercent(): GameContentDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 100,
      timeLeftSeconds: 0
    };
  }
}
