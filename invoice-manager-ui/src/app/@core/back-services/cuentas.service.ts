import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Cuenta } from '../../models/cuenta';


@Injectable({
  providedIn: 'root',
})
export class CuentasService {

  constructor(private httpClient: HttpClient) { }

  public getCuentasByParams(page: number, size: number, filterParams?: any): Observable<any>{
    let pageParams : HttpParams =  new HttpParams().append('page',page.toString()).append('size',size.toString());
    for (const key in filterParams) {
      let value = filterParams[key];
      if(value.length>0){
        pageParams = pageParams.append(key, (filterParams[key]==='*')?'':value);
      }
    }
    return this.httpClient.get('../api/cuentas', { params: pageParams });
  }

  public getCuentasReport(page: number, size: number, filterParams?: any): Observable<any>{
    let pageParams : HttpParams =  new HttpParams().append('page',page.toString()).append('size',size.toString());
    for (const key in filterParams) {
      let value = filterParams[key];
      if(value.length>0){
        pageParams = pageParams.append(key, (filterParams[key]==='*')?'':value);
      }
    }
    return this.httpClient.get('../api/cuentas/report', { params: pageParams });

  }
  public getCuentasByCompany(companyRfc: string): Observable<any>{
    return this.httpClient.get(`../api/empresas/${companyRfc}/cuentas`);
  }

  public getCuentaInfo(empresa:string,cuenta:string): Observable<any>{
    return this.httpClient.get(`../api/cuenta/${empresa}/${cuenta}`,);
  }

  public updateCuenta(cuenta: Cuenta): Observable<Object> {
    return this.httpClient.put(`../api/cuentas/${cuenta.id}`, cuenta);
  }

  public insertCuenta(cuenta: Cuenta): Observable<Object> {
    return this.httpClient.post('../api/cuentas', cuenta);
  }

  public deleteCuenta(id: string): Observable<Object> {
    return this.httpClient.delete(`../api/cuentas/${id}`);
  }


}
