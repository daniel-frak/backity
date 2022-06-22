export * from './downloads.service';
import {DownloadsClient} from './downloads.service';
import {FileDiscoveryClient} from './fileDiscovery.service';
import {GOGClient} from './gOG.service';
import {GOGAuthenticationClient} from './gOGAuthentication.service';
import {H2DatabaseClient} from './h2Database.service';
import {LogsClient} from './logs.service';

export * from './fileDiscovery.service';
export * from './gOG.service';
export * from './gOGAuthentication.service';
export * from './h2Database.service';
export * from './logs.service';
export const APIS = [DownloadsClient, FileDiscoveryClient, GOGClient, GOGAuthenticationClient, H2DatabaseClient, LogsClient];
