import { Factura } from '../../models/factura/factura';
import { GenericPage } from '../../models/generic-page';
import { Observable } from 'rxjs';

export abstract class InvoicesData {

    abstract getInvoices(page:number,size:number,filterParams?:any) : Observable<GenericPage<Factura>>;

    abstract getInvoiceByFolio(folio:string) : Observable<Factura>;

    abstract getInvoiceById(id:number) : Observable<[Factura]>;

    abstract insertNewInvoice(invoice : Factura) : Observable<Factura>;

    abstract updateInvoice(invoice : Factura) : Observable<Factura>;
  
}