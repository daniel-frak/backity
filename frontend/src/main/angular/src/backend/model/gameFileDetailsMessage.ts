/**
 * backend
 * The backend module built with Spring Boot
 *
 * The version of the OpenAPI document: 0.0.1-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import {BackupDetailsMessage} from './backupDetailsMessage';
import {SourceFileDetailsMessage} from './sourceFileDetailsMessage';


export interface GameFileDetailsMessage {
    id?: string;
    gameId?: string;
    sourceFileDetails?: SourceFileDetailsMessage;
    backupDetails?: BackupDetailsMessage;
    dateCreated?: string;
    dateModified?: string;
}

