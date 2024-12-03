import { RxStompConfig } from '@stomp/rx-stomp';
import {environment} from "@environment/environment";

export const rxStompConfig: RxStompConfig = {
  brokerURL: environment.apiUrl.replace("http", "ws") + '/messages'
};
