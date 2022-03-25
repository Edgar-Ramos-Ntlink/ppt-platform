import { BigNumber } from "mathjs";
import { InformacionAduanera } from "./informacion-aduanera";

export class Parte {

    public informacionAduanera: InformacionAduanera;
    public noIdentificacion: string;
    public claveProdServ: string;
    public cantidad: BigNumber;
    public unidad: string;
    public descripcion: string;
    public valorUnitario: BigNumber;
    public importe: BigNumber;

}