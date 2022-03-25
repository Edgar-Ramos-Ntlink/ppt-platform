import { BigNumber } from "mathjs";
import { ACuentaTerceros } from "./a-cuenta-terceros";
import { CuentaPredial } from "./cuenta-predial";
import { Impuesto } from "./impuesto";
import { InformacionAduanera } from "./informacion-aduanera";
import { Parte } from "./parte";

export class Concepto {

  public claveProdServ: string;
  public noIdentificacion: string;
  public cantidad: BigNumber;
  public claveUnidad: string;
  public unidad: string;
  public descripcion: string;
  public valorUnitario: BigNumber;
  public importe: BigNumber;
  public descuento: BigNumber;
  public objetoImp: string;
  public impuestos: Impuesto[];

  public aCuentaTerceros: ACuentaTerceros;
  public informacionAduanera: InformacionAduanera;
  public cuentaPredial: CuentaPredial;
  public parte: Parte;
}