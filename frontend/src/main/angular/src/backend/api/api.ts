export * from './fileDetails.service';
import {FileDetailsClient} from './fileDetails.service';
import {FileDiscoveryClient} from './fileDiscovery.service';
import {GOGClient} from './gOG.service';
import {GOGAuthenticationClient} from './gOGAuthentication.service';
import {GamesClient} from './games.service';
import {H2DatabaseClient} from './h2Database.service';
import {LogsClient} from './logs.service';

export * from './fileDiscovery.service';
export * from './gOG.service';
export * from './gOGAuthentication.service';
export * from './games.service';
export * from './h2Database.service';
export * from './logs.service';
export const APIS = [FileDetailsClient, FileDiscoveryClient, GOGClient, GOGAuthenticationClient, GamesClient, H2DatabaseClient, LogsClient];
