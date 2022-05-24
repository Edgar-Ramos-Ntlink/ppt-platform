export class Receptor {
  public rfc: string;
  public nombre: string;
  public domicilioFiscalReceptor: string;
  public residenciaFiscal: string;
  public numRegIdTrib: string;
  public regimenFiscalReceptor: string;
  public usoCfdi: string;

  constructor() {
    this.usoCfdi = "*";
    this.regimenFiscalReceptor = "*"
  }
}
