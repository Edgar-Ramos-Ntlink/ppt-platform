import { Cuenta } from '../../models/cuenta';
import { GenericPage } from '../../models/generic-page';
import { Observable } from 'rxjs';
import { ResourceFile } from '../../models/resource-file';

export abstract class CuentasData {
    abstract getCuentasByParams(page: number, size: number, filterParams?: any): Observable<GenericPage<Cuenta>>;
    abstract getCuentasReport(page: number, size: number, filterParams?: any): Observable<ResourceFile>;
    abstract getCuentasByCompany(companyRfc: string): Observable<Cuenta[]>;

    abstract getCuentaInfo(empresa:string,cuenta:string): Observable<Cuenta>;
    abstract updateCuenta(cuenta: Cuenta): Observable<Cuenta>;
    abstract insertCuenta(cuenta: Cuenta): Observable<Cuenta>;
    abstract deleteCuenta(id: string): Observable<Cuenta>;
}
