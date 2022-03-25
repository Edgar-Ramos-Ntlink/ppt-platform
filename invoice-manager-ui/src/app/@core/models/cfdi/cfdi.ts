import { BigNumber } from "mathjs";
import { CfdiRelacionados } from "./cfdi-relacionados";
import { Concepto } from "./concepto";
import { Emisor } from "./emisor";
import { Impuesto } from "./impuesto";
import { InformacionGlobal } from "./informacion-global";
import { Receptor } from "./receptor";

export class Cfdi{
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
 public subtotal: BigNumber;
 public descuento: BigNumber;
 public total: BigNumber;
 public moneda: string;
 public tipoCambio: BigNumber;
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
 public complemento :any[];
 public addenda: any[];
}