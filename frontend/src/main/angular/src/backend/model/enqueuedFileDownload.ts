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


export interface EnqueuedFileDownload { 
    id?: number;
    url?: string;
    name?: string;
    gameTitle?: string;
    version?: string;
    size?: string;
    dateCreated?: string;
    downloaded?: boolean;
    failed?: boolean;
}
