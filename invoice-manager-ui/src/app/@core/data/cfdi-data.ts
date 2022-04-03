import { Observable } from 'rxjs';
import { Factura } from '../../models/factura';
import { Cfdi } from '../models/cfdi/cfdi';
import { Concepto } from '../models/cfdi/concepto';
import { Pago } from '../models/cfdi/pago';

export abstract class CfdiData {

    abstract getCfdiByFolio(prefolio: number): Observable<Cfdi>;
    abstract updateCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract calcularMontosCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract insertConcepto(prefolio: number, concepto: Concepto): Observable<Concepto>;
    abstract updateConcepto(prefolio: number, id: number, concepto: Concepto): Observable<Concepto>;
    abstract deleteConcepto(prefolio: number, conceptoId: number): Observable<any>;
    abstract findPagosPPD(prefolio: number): Observable<Pago[]>;
    abstract getFacturaInfo(prefolio: number): Observable<Factura>;
    abstract getChildrenCfdi(folio: string, parcialidad: number): Observable<Factura>;

}
