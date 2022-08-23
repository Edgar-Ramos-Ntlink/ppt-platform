import { Client } from "./client";

export class Devolucion {

    public moneda:string;
    public promotor:string;
    public clientes: Client[];
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
    public detalles : DetalleDevolucion[];
    public pagos: any[];

    constructor(){
        this.moneda ='MXN';
        this.detalles = [];
        this.porcentajeContacto = 0;
        this.porcentajeDespacho = 0;
        this.procentajeCliente = 0;
        this.porcentajePromotor = 0;
        this.clientes = [];
        this.pagos = [];
    }

}

export declare type TipoDevolucion  = 'CLIENTE' | 'PROMOTOR' | 'CONTACTO' | 'DESPACHO';

export class DetalleDevolucion {

    public id : number;
    public tipo : TipoDevolucion;
    public monto: number;
    public porcentaje: number;
    public pagos : ReferenciaDevolucion[];

    constructor(){
        this.pagos = [];
        this.monto = 0;
    }

}

export class ReferenciaDevolucion {
    public id: number;
    public receptorPago: string;
    public formaPago: string;
    public monto: number;
    public notas : number;
}