import { Retencion } from "./retencion";
import { Traslado } from "./traslado";

export class Impuesto {
  public traslados: Traslado[];
  public retenciones: Retencion[];
  public totalImpuestosTrasladados: number;
  public totalImpuestosRetenidos: number;

  constructor() {
    this.totalImpuestosTrasladados = 0;
    this.totalImpuestosRetenidos = 0;
  }
}
