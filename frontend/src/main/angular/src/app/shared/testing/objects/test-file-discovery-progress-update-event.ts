import {FileDiscoveryProgressUpdateEvent} from "@backend";

export class TestFileDiscoveryProgressUpdateEvent {

  public static twentyFivePercent(): FileDiscoveryProgressUpdateEvent {
    return {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };
  }
}
