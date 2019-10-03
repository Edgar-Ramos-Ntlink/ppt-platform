import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http'
import { Empresa } from '../../models/empresa';

@Injectable({
  providedIn: 'root'
})
export class CompaniesService {

  constructor(private httpClient:HttpClient) { }


  public getCompanies(page: number, size: number, filterParams?: any): Observable<Object> {
    let pageParams : HttpParams =  new HttpParams().append('page',page.toString()).append('size',size.toString());
    for (const key in filterParams) {
      const value : string = filterParams[key];
      if(value.length>0){
        pageParams = pageParams.append(key, filterParams[key]);
      }
    }
    return this.httpClient.get('../api/empresas',{params:pageParams});
  }

  public getCompanyByRFC(rfc: string): Observable<Object> {
    return this.httpClient.get(`../api/empresas/${rfc}`);
  }

  public insertNewCompany(empresa: Empresa): Observable<Object> {
    return this.httpClient.post('../api/empresas',empresa);
  }

  public updateCompany(empresa: Empresa): Observable<Object> {
    return this.httpClient.put(`../api/empresas/${empresa.informacionFiscal.rfc}`,empresa);
  }
}
