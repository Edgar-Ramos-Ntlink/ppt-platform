import { Observable } from 'rxjs';
import { Cfdi } from '../models/cfdi/cfdi';

export abstract class CfdiData {
    abstract recalculateCfdi(cfdi: Cfdi): Observable<Cfdi>;
}
