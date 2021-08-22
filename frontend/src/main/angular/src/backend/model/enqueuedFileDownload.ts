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
import { DownloadStatus } from './downloadStatus';


export interface EnqueuedFileDownload { 
    id?: number;
    source: string;
    url: string;
    name: string;
    gameTitle: string;
    version: string;
    size: string;
    dateCreated: string;
    status: DownloadStatus;
    failedReason?: string;
}

