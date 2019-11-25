import { Factura } from '../../models/factura/factura';
import { GenericPage } from '../../models/generic-page';
import { Observable } from 'rxjs';

export abstract class InvoicesData {

    abstract getInvoices(page:number,size:number,filterParams?:any) : Observable<GenericPage<Factura>>;

    abstract getInvoiceByFolio(folio:string) : Observable<Factura>;
    
    abstract getInvoiceFiles(folio:string) : Observable<any>;

    abstract getComplementosInvoice(folioPadre:string) : Observable<Factura[]>;

    abstract timbrarFactura(folio:string,factura:Factura) : Observable<any>;

    abstract cancelarFactura(folio:string,factura:Factura) : Observable<any>;

    abstract insertNewInvoice(invoice : Factura) : Observable<Factura>;

    abstract updateInvoice(invoice : Factura) : Observable<Factura>;

    
}