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
import {Pageable} from './pageable';
import {DiscoveredFile} from './discoveredFile';
import {Sort} from './sort';


export interface PageDiscoveredFile {
    totalPages?: number;
    totalElements?: number;
    size?: number;
    content?: Array<DiscoveredFile>;
    number?: number;
    sort?: Sort;
    first?: boolean;
    last?: boolean;
    numberOfElements?: number;
    pageable?: Pageable;
    empty?: boolean;
}

