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


export type FileStatus = 'DISCOVERED' | 'ENQUEUED_FOR_DOWNLOAD' | 'DOWNLOAD_IN_PROGRESS' | 'DOWNLOADED' | 'DOWNLOAD_FAILED';

export const FileStatus = {
    Discovered: 'DISCOVERED' as FileStatus,
    EnqueuedForDownload: 'ENQUEUED_FOR_DOWNLOAD' as FileStatus,
    DownloadInProgress: 'DOWNLOAD_IN_PROGRESS' as FileStatus,
    Downloaded: 'DOWNLOADED' as FileStatus,
    DownloadFailed: 'DOWNLOAD_FAILED' as FileStatus
};

