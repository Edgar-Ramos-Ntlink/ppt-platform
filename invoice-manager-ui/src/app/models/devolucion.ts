import { Client } from "./client";

export class Devolucion {

    public id : number
    public estado : StatusDevolucion;
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
    public actualizacion: string;
    public creacion: string;

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

export class ReferenciaDevolucion {
    public id: number;
    public estadoDevolucion :  StatusDevolucion;
    public estadoPago : StatusRefDevolucion;
    public receptorPago: TipoDevolucion;
    public formaPago: FormaPagoDevolucion;
    public monto: number;
    public notas : string;
    public actualizacion: string;
    public creacion: string;

    constructor(receptor?:TipoDevolucion, monto?:number){
        this.receptorPago = receptor;
        this.monto = monto;
        this.formaPago = 'TRANSFERENCIA';
    }
}

export declare type StatusDevolucion = 'VALIDACION' | 'VALIDADA' | 'COMPLETADA' | 'DESCARTADA';
export declare type StatusRefDevolucion  = 'PENDIENTE' | 'PAGADO';
export declare type TipoDevolucion  = 'CLIENTE' | 'PROMOTOR' | 'CONTACTO' | 'DESPACHO';
export declare type FormaPagoDevolucion = 'TRANSFERENCIA' | 'EFECTIVO' | 'NOMINA' | 'PENDIENTE' | 'OTRO' ;