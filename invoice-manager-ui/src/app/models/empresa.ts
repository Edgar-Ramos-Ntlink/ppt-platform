import { Contribuyente } from './contribuyente';
import { Cuenta } from './cuenta';
import { Observacion } from './observacion';
import { ResourceFile } from './resource-file';

export class Empresa {
    public id: number;


    // PASO 1 carga de datos genericos
    // datos genenerales
    public activo: boolean;

    public estatus:string; // Se calculara automaticamnete basado entre representanteLegal & correo & cert & key & cuentas
    public giro: string;
    public tipo: string; // linea empresa
    public regimenFiscal: string;
    public rfc: string;
    public nombre: string; // nombre corto
    public razonSocial: string;
    public calle: string;
    public noInterior: number;
    public noExterior: number;
    public municipio: string;
    public estado: string;
    public pais: string;
    public colonia: string;
    public cp: string;

    // OPCIONAL, puede ser realizado despues de paso 1
    // legal
    public anioAlta: number;
    public registroPatronal: string;
    public estatusJuridico: string;
    public estatusJuridico2: string;
    public representanteLegal: string;
    public ciec: string; //  Clave de Identificación Electrónica Confidencial (CIEC). Ahora llamada solo Contraseña del SAT


    // PASO 2 carga de datos digitales
    // datos digitales
    public web: string;
    public correo: string;
    public pwCorreo: string;
    public dominioCorreo: string;

    
    public pwSat: string; // talvez sea reemplazada por  FIEL (Firma Electrónica Avanzada), que es una contrasenia para timbrado
    public noCertificado: string;
    public expiracionCertificado: Date;
    
    
    // OPCIONAL, puede ser realizado despues de paso 1
    // contabilidad

    public actividadSAT: string;
    public fiel: string;
    
    public ingresos : Ingresos[]; // lista de objetos con ingreosos anuales empresa


    public creador: string;
    public fechaCreacion: Date;
    public fechaActualizacion: Date;
    public informacionFiscal: Contribuyente; // Se refactiorizara como elemento raiz
    
    
    public documentos: ResourceFile[];
    // DOCUMENTOS
    //public logotipo: string;  se adjunta como documento
    //public llavePrivada: string; se adjunta como documento
    //public certificado: string; se adjunta como documento
    // Archivo CSD 
    // Acta constitutiva
    // comprobante domicilio empresa
    // INE representate legal

    // OPCIONAL, puede ser realizado despues de paso 1
    public observaciones : Observacion[];

    // OPCIONAL, puede ser realizado despues de paso 1
    public cuentas : Cuenta[];

    constructor() {
        this.activo = false;
        this.estatus = 'INACTIVO';
        this.tipo= '*';
        this.giro='*';
        this.colonia = '*'
        this.regimenFiscal = '*';
        this.actividadSAT = "Otros servicios profesionales, científicos y técnicos 100%";
    }
}
//TODO extraer clase o a ver 
export class Ingresos{
    ano: string;
    cantidad: number;
    constructor(ano: string, cantidad: number){
        this.ano = ano;
        this.cantidad = cantidad;
    }
}