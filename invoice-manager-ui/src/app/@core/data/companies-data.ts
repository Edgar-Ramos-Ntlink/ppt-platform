import { Observable } from 'rxjs';
import { IngresoEmpresa } from '../../models/ingreso-empresa';
import { DetalleEmpresa } from '../../models/detalle-empresa';
import { Empresa } from '../../models/empresa';
import { GenericPage } from '../../models/generic-page';
import { ResourceFile } from '../../models/resource-file';

export abstract class CompaniesData {

    abstract getCompanies(filterParams?: any): Observable<GenericPage<Empresa>>;

    abstract getCompaniesReport(filterParams?: any): Observable<ResourceFile>;

    abstract getCompaniesByLineaAndGiro(linea: string, giro: number): Observable<Empresa[]>;

    abstract getCompanyByRFC(rfc: string): Observable<Empresa>;

    abstract insertNewCompany(empresa: Empresa): Observable<Empresa>;

    abstract updateCompany(rfc: string, empresa: Empresa): Observable<Empresa>;

    abstract getCompaniesDetails(rfc: string, type : string): Observable<DetalleEmpresa[]>;

    abstract insertCompanyDetail(detail: DetalleEmpresa): Observable<DetalleEmpresa>;

    abstract updateCompanyDetail(detail: DetalleEmpresa): Observable<DetalleEmpresa>;

    abstract deleteCompanyDetail(detailId: number): Observable<void>;

    abstract getCompanyIncomes(rfc: string): Observable<IngresoEmpresa[]>;

    abstract insertCompanyIncome(detail: IngresoEmpresa): Observable<IngresoEmpresa>;

    abstract deleteCompanyIncome(rfc:string, id: number): Observable<void>;
}