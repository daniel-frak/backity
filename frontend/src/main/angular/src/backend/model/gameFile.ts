/**
 * backend
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { GameProviderFile } from './gameProviderFile';
import { FileBackup } from './fileBackup';


export interface GameFile { 
    id: string;
    gameId: string;
    gameProviderFile: GameProviderFile;
    fileBackup: FileBackup;
    dateCreated?: string;
    dateModified?: string;
}
