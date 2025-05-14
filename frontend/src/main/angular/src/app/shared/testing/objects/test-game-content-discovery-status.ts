import {GameContentDiscoveryStatus} from "@backend";

export class TestGameContentDiscoveryStatus {

  public static inProgress(): GameContentDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: true
    };
  }

  public static notInProgress(): GameContentDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: false
    };
  }
}
