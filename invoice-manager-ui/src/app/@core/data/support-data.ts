import { Observable } from 'rxjs';
import { ResourceFile } from '../../models/resource-file';
import { SupportRequest } from '../../models/support-request';

export abstract class SupportData {
    abstract insertSoporte(soporte: SupportRequest): Observable<SupportRequest>;

    abstract updateSoporte(
        idSoporte: number,
        soporte: SupportRequest
    ): Observable<SupportRequest>;

    abstract buscarSoporte(folio: number): Observable<SupportRequest>;

    abstract insertAttachedFile(
        folio: number,
        file: ResourceFile
    ): Observable<any>;

    abstract getAttachedDocument(folio: number): Observable<ResourceFile>;
}
