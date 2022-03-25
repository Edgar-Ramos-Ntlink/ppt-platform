import { BigNumber } from "mathjs";


export class Traslado{

  public base: BigNumber;
  public impuesto: string;
  public tipoFactor: string;
  public tasaOCuota: BigNumber;
  public importe: BigNumber;
}