import { Observable } from 'rxjs';
import { Cfdi } from '../models/cfdi/cfdi';
import { Pago } from '../models/cfdi/pago';
import { Factura } from '../models/factura';

export abstract class CfdiData {
    abstract getCfdiByFolio(prefolio: string): Observable<Cfdi>;
    abstract updateCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract recalculateCfdi(cfdi: Cfdi): Observable<Cfdi>;
    abstract getFacturaInfo(prefolio: number): Observable<Factura>;
}
