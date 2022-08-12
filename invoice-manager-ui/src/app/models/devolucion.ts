import { PagoFactura } from "./pago-factura";
import { ReferenciaDevolucion } from "./referencia-devolucion";

export class Devolucion {

    public moneda:string;
    public promotor:string;
    public rfcCliente: string;
    public nombreCliente: string;
    public total:number;
    public porcentajeDespacho: number;
    public montoDespacho: number;
    public porcentajePromotor: number;
    public montoPromotor: number;
    public procentajeCliente: number;
    public montoCliente: number;
    public porcentajeContacto: number; 
    public montoContacto: number; 
    public detalles : ReferenciaDevolucion[];
    public pagos: any[];

    constructor(){
        this.moneda ='MXN';
        this.detalles = [];
    }

}