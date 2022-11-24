import { Observable } from 'rxjs';
import { GenericPage } from '../../models/generic-page';
import { ResourceFile } from '../../models/resource-file';
import { ClientSupport } from '../../models/client-support';

export abstract class SupportData {
    abstract insertSoporte(soporte: ClientSupport): Observable<ClientSupport>;

    abstract updateSoporte(idSoporte: number, soporte: ClientSupport):  Observable<ClientSupport>;

    abstract buscarSoporte(idSoporte: number):  Observable<ClientSupport>;

    abstract getSoportes(filterParams: any): Observable<GenericPage<ClientSupport>>;

    abstract getSoporteReport(filterParams: any): Observable<ResourceFile>;

    abstract insertAttachedFile(folio: number, file: ResourceFile): Observable<any>;

    abstract getAttachedDocument(folio: number): Observable<ResourceFile>;
}
