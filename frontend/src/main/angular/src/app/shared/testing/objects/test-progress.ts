import {Progress} from "@backend";

export namespace TestProgress {

  export function twentyFivePercent(): Progress {
    return {
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
