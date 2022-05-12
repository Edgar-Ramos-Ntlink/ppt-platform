import { CfdiRelacionados } from './cfdi-relacionados';
import { Concepto } from './concepto';
import { Emisor } from './emisor';
import { Impuesto } from './impuesto';
import { InformacionGlobal } from './informacion-global';
import { Receptor } from './receptor';

export class Cfdi {
    public version: string;
    public serie: string;
    public folio: string;
    public fecha: string;
    public sello: string;
    public formaPago: string;
    public metodoPago: string;
    public noCertificado: string;
    public certificado: string;
    public condicionesDePago: string;
    public subtotal: number;
    public descuento: number;
    public total: number;
    public moneda: string;
    public tipoCambio: number;
    public tipoDeComprobante: string;
    public exportacion: string;
    public lugarExpedicion: string;
    public confirmacion: string;

    public informacionGlobal: InformacionGlobal;
    public cfdiRelacionados: CfdiRelacionados;
    public emisor: Emisor;
    public receptor: Receptor;

    public conceptos: Concepto[];
    public impuestos: Impuesto[];
    public complemento: any[];
    public addenda: any[];

    constructor() {
        this.version = '4.0';
        this.tipoDeComprobante = 'I';
        this.conceptos = [];
        this.impuestos = [];
        this.complemento = [];
        this.receptor = new Receptor();
        this.emisor = new Emisor();
        this.total = 0;
        this.subtotal = 0;
        this.descuento = 0;
        this.tipoCambio = 1.0;
        this.metodoPago = 'PUE';
        this.formaPago = '99';
        this.moneda = 'MXN';
        this.exportacion = '01';
    }
}
