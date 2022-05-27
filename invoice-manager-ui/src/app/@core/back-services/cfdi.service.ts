import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Cfdi } from '../models/cfdi/cfdi';

@Injectable({
    providedIn: 'root',
})
export class CfdiService {
    constructor(private httpClient: HttpClient) {}

    public recalculateCfdi(cfdi: Cfdi): Observable<any> {
        return this.httpClient.put(`../api/cfdis/recalculate`, cfdi);
    }
}
