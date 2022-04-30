import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Cfdi } from '../models/cfdi/cfdi';

@Injectable({
  providedIn: 'root',
})
export class CfdiService {

  constructor(private httpClient: HttpClient) { }

  public getCfdiByFolio(prefolio: string): Observable<any> {
    return this.httpClient.get(`../api/cfdis/${prefolio}`);
  }

  public updateCfdi(cfdi: Cfdi): Observable<any> {
    return this.httpClient.put(`../api/cfdis/${cfdi.folio}`, cfdi);
  }

  public recalculateCfdi(cfdi: Cfdi): Observable<any> {
    return this.httpClient.put(`../api/cfdis/recalculate`, cfdi);
  }

  public findInvoicePaymentComplementsByFolio(folio: string): Observable<any> {
    return this.httpClient.get(`../api/cfdis/${folio}/payments`);
  }

  public findPagosPPD(prefolio: number): Observable<any> {
    return this.httpClient.get(`../api/cfdis/${prefolio}/pagos`);
  }
  public getFacturaInfo(prefolio: number): Observable<any> {
    return this.httpClient.get(`../api/cfdis/${prefolio}/facturaInfo`);
  }

  public getChildrenCfdi(prefolio: string, parcialidad:number): Observable<any> {
    return this.httpClient.get(`../api/facturas/complementos/${prefolio}?parcialidad=${parcialidad}`);
   
  }



}
