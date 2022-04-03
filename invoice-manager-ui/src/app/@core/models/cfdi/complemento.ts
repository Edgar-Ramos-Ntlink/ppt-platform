import { Pago } from "./pago";
import { TimbreFiscal } from "./timbre-fiscal";

export class Complemento {

    public timbreFiscal: TimbreFiscal;
    public pagos: Pago[];

    constructor() {
        this.pagos = [];
    }
}
