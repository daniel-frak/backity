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


export type FileBackupMessageTopics = '/topic/backups/started' | '/topic/backups/progress-update' | '/topic/backups/status-changed';

export const FileBackupMessageTopics = {
    Started: '/topic/backups/started' as FileBackupMessageTopics,
    ProgressUpdate: '/topic/backups/progress-update' as FileBackupMessageTopics,
    StatusChanged: '/topic/backups/status-changed' as FileBackupMessageTopics
};

