import { Contribuyente } from './contribuyente';
import { Cuenta } from './cuenta';
import { Observacion } from './observacion';
import { ResourceFile } from './resource-file';

export class Empresa {
    public id: number;
    public referencia: string;
    public regimenFiscal: string;
    public web: string;
    public contactoAdmin: string;
    public pwSat: string;
    public pwCorreo: string;
    public correo: string;
    public encabezado: string;
    public piePagina: string;
    public activo: boolean;
    public tipo: string;
    public giro: string;
    public logotipo: string;
    public llavePrivada: string;
    public certificado: string;
    public fechaCreacion: Date;
    public fechaActualizacion: Date;
    public informacionFiscal: Contribuyente;
    public sucursal: string;
    public lugarExpedicion: string;
    public noCertificado: string;

    public nombreCorto: string;

    public estatusJuridico: string;
    public estatusJuridico2: string;
    public representanteLegal: string;

    public documentos: ResourceFile[];

    public observaciones : Observacion[];

    public cuentas : Cuenta[];

    constructor() {
        this.informacionFiscal = new Contribuyente();
    }
}