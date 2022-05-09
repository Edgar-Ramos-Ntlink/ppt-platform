export class Traslado{

  public base: number;
  public impuesto: string;
  public tipoFactor: string;
  public tasaOCuota: string;
  public importe: number;

  constructor(impuesto:string,tipoFactor:string,tasaOcuota:string,base:number, importe:number){
    this.impuesto = impuesto;
    this.tipoFactor = tipoFactor;
    this.tasaOCuota = tasaOcuota;
    this.base = base;
    this.importe = importe;
  }
}