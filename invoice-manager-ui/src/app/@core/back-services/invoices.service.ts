import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http'
import { Factura } from '../../models/factura/factura';

@Injectable({
  providedIn: 'root'
})
export class InvoicesService {

  constructor(private httpClient:HttpClient) { }

  public getInvoices(page: number, size: number, filterParams?: any): Observable<Object> {
    let pageParams : HttpParams =  new HttpParams().append('page',page.toString()).append('size',size.toString());
    for (const key in filterParams) {
      const value : string = filterParams[key];
      if(value.length>0){
        pageParams = pageParams.append(key, (filterParams[key]==='*')?'':filterParams[key]);
      }
    }
    return this.httpClient.get('../api/facturas',{params:pageParams});
  }

  public getInvoiceByFolio(folio:string) : Observable<any>{
    return this.httpClient.get(`../api/facturas/${folio}`);
  }

  public insertNewInvoice(invoice : Factura): Observable<any>{
    return this.httpClient.post('../api/facturas/chain',invoice);
  }
}
