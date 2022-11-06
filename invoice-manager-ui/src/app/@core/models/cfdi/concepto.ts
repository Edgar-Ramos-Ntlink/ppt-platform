import { ACuentaTerceros } from './a-cuenta-terceros';
import { CuentaPredial } from './cuenta-predial';
import { Impuesto } from './impuesto';
import { InformacionAduanera } from './informacion-aduanera';
import { Parte } from './parte';

export class Concepto {
    public claveProdServ: string; // required
    public claveProdServDesc: string; // required
    public noIdentificacion: string;
    public cantidad: number; //required
    public claveUnidad: string; //required
    public unidad: string; // optional
    public descripcion: string; // required
    public valorUnitario: number; //required
    public importe: number; //required
    public descuento: number; // optional
    public objetoImp: string; // optional
    public impuestos: Impuesto[]; //required

    public aCuentaTerceros: ACuentaTerceros;
    public informacionAduanera: InformacionAduanera;
    public cuentaPredial: CuentaPredial;
    public parte: Parte;

    public impuesto: string; // only used on UI

    constructor() {
        this.impuestos = [];
        this.descuento = 0;
        this.importe = 0;
        this.valorUnitario = 0;
        this.cantidad = 1;
        this.claveProdServ = '';
        this.claveProdServDesc = '';
        this.claveUnidad = 'E48';
        this.unidad = 'Unidad de Servicio';
        this.descripcion = '';
        this.objetoImp = '02';
        this.impuesto = 'IVA_0.16';
    }
}
