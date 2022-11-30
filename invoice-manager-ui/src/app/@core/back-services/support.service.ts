import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ResourceFile } from '../../models/resource-file';
import { SupportRequest } from '../../models/support-request';

@Injectable({
    providedIn: 'root',
})
export class SupportService {
    constructor(private httpClient: HttpClient) {}

    private getHttpParams(filterParams: any): HttpParams {
        let pageParams: HttpParams = new HttpParams();
        for (const key in filterParams) {
            if (filterParams[key] !== undefined) {
                const value: string = filterParams[key].toString();
                if (value !== null && value.length > 0 && value !== '*') {
                    pageParams = pageParams.append(key, value);
                }
            }
        }
        return pageParams;
    }

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

    public getSoportes(filterParams: any): Observable<any> {
        return this.httpClient.get(`../api/support`, {
            params: this.getHttpParams(filterParams),
        });
    }

    public getSoporteReport(filterParams: any): Observable<any> {
        return this.httpClient.get(`../api/support/report`, {
            params: this.getHttpParams(filterParams),
        });
    }
}
