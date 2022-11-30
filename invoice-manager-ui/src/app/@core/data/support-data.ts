import { Observable } from 'rxjs';
import { GenericPage } from '../../models/generic-page';
import { ResourceFile } from '../../models/resource-file';
import { SupportRequest } from '../../models/support-request';

export abstract class SupportData {
    abstract insertSoporte(soporte: SupportRequest): Observable<SupportRequest>;

    abstract updateSoporte(
        idSoporte: number,
        soporte: SupportRequest
    ): Observable<SupportRequest>;

    abstract buscarSoporte(idSoporte: number): Observable<SupportRequest>;

    abstract getSoportes(
        filterParams: any
    ): Observable<GenericPage<SupportRequest>>;

    abstract getSoporteReport(filterParams: any): Observable<ResourceFile>;
}
