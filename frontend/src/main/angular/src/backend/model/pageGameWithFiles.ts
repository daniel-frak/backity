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
import {PageableObject} from './pageableObject';
import {SortObject} from './sortObject';
import {GameWithFiles} from './gameWithFiles';


export interface PageGameWithFiles {
    totalPages?: number;
    totalElements?: number;
    size?: number;
    content?: Array<GameWithFiles>;
    number?: number;
    sort?: SortObject;
    numberOfElements?: number;
    first?: boolean;
    last?: boolean;
    pageable?: PageableObject;
    empty?: boolean;
}

