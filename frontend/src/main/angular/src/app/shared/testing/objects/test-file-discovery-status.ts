import {FileDiscoveryStatus} from "@backend";

export class TestFileDiscoveryStatus {

  public static inProgress(): FileDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: true
    };
  }

  public static notInProgress(): FileDiscoveryStatus {
    return {
      gameProviderId: 'someGameProviderId',
      isInProgress: false
    };
  }
}
