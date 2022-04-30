import { Observable } from 'rxjs';
import { Factura } from '../../models/factura';
import { Cfdi } from '../models/cfdi/cfdi';
import { Pago } from '../models/cfdi/pago';
import { ComplementoPago } from '../models/complemento-pago';

export abstract class CfdiData {

    abstract getCfdiByFolio(prefolio: string): Observable<Cfdi>;
    abstract updateCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract recalculateCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract findInvoicePaymentComplementsByFolio(folio: string): Observable<ComplementoPago[]>;


    abstract findPagosPPD(prefolio: number): Observable<Pago[]>;
    abstract getFacturaInfo(prefolio: number): Observable<Factura>;
    abstract getChildrenCfdi(folio: string, parcialidad: number): Observable<Factura>;

}
