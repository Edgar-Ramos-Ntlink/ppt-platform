import { Cfdi } from './cfdi/cfdi';
import { ComplementoPago } from './complemento-pago';

export class Factura {
    public id: number;
    public idCfdi: number;
    public rfcEmisor: string;
    public razonSocialEmisor: string;
    public lineaEmisor: string;
    public direccionEmisor: string;

    public rfcRemitente: string;
    public lineaRemitente: string;
    public razonSocialRemitente: string;
    public direccionReceptor: string;
    public tipoDocumento: string;
    public solicitante: string;
    public metodoPago: string;
    public folio: string;
    public preFolio: string;
    public uuid: string;
    public notas: string;
    public statusFactura: string;
    public statusDetail: string;
    public statusCancelacion: string;
    public fechaCancelacion: string;
    public fechaCreacion: Date;
    public fechaActualizacion: Date;
    public fechaTimbrado: Date;
    public packFacturacion: string;
    public saldoPendiente: number;
    public total: number;
    public montoTotalDesc: string;
    public cfdi: Cfdi;
    public folioRelacionado: number;
    public folioRelacionadoPadre: number;
    public validacionTeso: boolean;
    public validacionOper: boolean;
    public pagos: ComplementoPago[];

    public motivo: string;
    public folioSustituto: string;
    public version: string;

    constructor() {
        this.tipoDocumento = 'Factura';
        this.statusFactura = '1';
        this.validacionOper = false;
        this.validacionTeso = false;
        this.statusDetail = '';
        this.lineaEmisor = 'A';
        this.lineaRemitente = 'CLIENTE';
        this.packFacturacion = 'NTLINK';
        this.cfdi = new Cfdi();
        this.pagos = [];
    }
}
