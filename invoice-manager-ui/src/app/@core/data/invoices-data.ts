import { Factura } from '../../models/factura';
import { GenericPage } from '../../models/generic-page';
import { Observable } from 'rxjs';
import { ResourceFile } from '../../models/resource-file';
import { Pago } from '../models/cfdi/pago';

export abstract class InvoicesData {
    abstract getInvoices(filterParams: any): Observable<GenericPage<Factura>>;

    abstract getInvoicesReports(filterParams: any): Observable<ResourceFile>;
    abstract getComplementReports(filterParams: any): Observable<ResourceFile>;

    abstract getInvoiceSaldo(prefolio: string): Observable<number>;

    abstract getInvoiceByFolio(folio: string): Observable<Factura>;
    abstract getInvoiceFiles(folio: string): Observable<any>;
    abstract getComplementosInvoice(folioPadre: string): Observable<Factura[]>;
    abstract timbrarFactura(folio: string, factura: Factura): Observable<any>;
    abstract cancelarFactura(folio: string, factura: Factura): Observable<any>;
    abstract insertNewInvoice(invoice: Factura): Observable<Factura>;
    abstract updateInvoice(invoice: Factura): Observable<Factura>;
    abstract generateInvoiceComplement(folioPadre: string, complemento: Pago): Observable<Factura>;
    abstract generateReplacement(folioFact: string,factura: Factura): Observable<Factura>;
    abstract generateCreditNote(folioFact: string,factura: Factura): Observable<Factura>;
    abstract reSendEmail(folio: string, factura: Factura): Observable<any>;
}
