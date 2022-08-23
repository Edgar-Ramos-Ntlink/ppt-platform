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
    public porcentajeContacto: number; 
    public montoContacto: number;
    public procentajeCliente: number;
    public montoCliente: number;
    public pasivoCliente: number;
    public comisionCliente : number; 
    public detalles : ReferenciaDevolucion[];
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

export declare type FormaPagoDevolucion = 'TRANSFERENCIA' | 'EFECTIVO' | 'NOMINA' | 'PENDIENTE' | 'OTRO' ;

export class ReferenciaDevolucion {
    public id: number;
    public receptorPago: TipoDevolucion;
    public formaPago: FormaPagoDevolucion;
    public monto: number;
    public notas : string;

    constructor(receptor?:TipoDevolucion, monto?:number){
        this.receptorPago = receptor;
        this.monto = monto;
        this.formaPago = 'TRANSFERENCIA';
    }
}