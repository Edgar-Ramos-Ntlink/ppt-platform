import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ResourceFile } from '../../models/resource-file';
import { SupportRequest } from '../../models/support-request';

@Injectable({
    providedIn: 'root',
})
export class SupportService {
    constructor(private httpClient: HttpClient) {}

    public insertSoporte(soporte: SupportRequest): Observable<any> {
        return this.httpClient.post(`../api/support`, soporte);
    }

    public updateSoporte(
        idSoporte: number,
        soporte: SupportRequest
    ): Observable<any> {
        return this.httpClient.put(`../api/support/${idSoporte}`, soporte);
    }

    public buscarSoporte(idSoporte: number): Observable<any> {
        return this.httpClient.get(`../api/support/${idSoporte}`);
    }

    public insertAttachedFile(
        folio: number,
        file: ResourceFile
    ): Observable<any> {
        return this.httpClient.post(`../api/support/${folio}/file`, file);
    }

    public getAttachedDocument(folio: number): Observable<any> {
        return this.httpClient.get(`../api/support/${folio}/file`);
    }
}
