import { Observable } from 'rxjs';
import { ResourceFile } from '../../models/resource-file';

export abstract class FilesData {
    abstract getFacturaFile(
        folio: string,
        tipoArchivo: string
    ): Observable<ResourceFile>;
    abstract getResourceFile(
        referencia: string,
        tipoRecurso: string,
        tipoArchivo: string
    ): Observable<ResourceFile>;
    abstract getResourceFileFromUrl(path: string): Observable<ResourceFile>;
    abstract getResourcesByTypeAndReference(
        type: string,
        referencia: string
    ): Observable<ResourceFile[]>;

    abstract insertFacturaFile(file: ResourceFile): Observable<ResourceFile>;
    abstract insertResourceFile(file: ResourceFile): Observable<ResourceFile>;
    abstract deleteResourceFile(id: number): Observable<any>;
}
