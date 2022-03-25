import { BigNumber } from "mathjs";
import { Retencion } from "./retencion";
import { Traslado } from "./traslado";

export class Impuesto {

    public traslados:Traslado[];
    public retenciones:Retencion[];
    public totalImpuestosTrasladados:BigNumber;
    public totalImpuestosRetenidos:BigNumber;


    constructor(){
        this.traslados = [];
        this.retenciones = [];
    }
}