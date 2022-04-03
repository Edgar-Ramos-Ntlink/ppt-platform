import { ACuentaTerceros } from "./a-cuenta-terceros";
import { CuentaPredial } from "./cuenta-predial";
import { Impuesto } from "./impuesto";
import { InformacionAduanera } from "./informacion-aduanera";
import { Parte } from "./parte";

export class Concepto {

  public id: number;

  public claveProdServ: string;
  public noIdentificacion: string;
  public cantidad: number;
  public claveUnidad: string;
  public unidad: string;
  public descripcion: string;
  public valorUnitario: number;
  public importe: number;
  public descuento: number;
  public objetoImp: string;
  public impuestos: Impuesto[];

  public aCuentaTerceros: ACuentaTerceros;
  public informacionAduanera: InformacionAduanera;
  public cuentaPredial: CuentaPredial;
  public parte: Parte;

  constructor(){
    this.cantidad = 0;
    this.valorUnitario = 1;
    this.importe = 1;
    this.importe = 1;
    this.impuestos = [];
  }
}