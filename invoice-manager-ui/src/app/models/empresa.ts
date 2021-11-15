import { Contribuyente } from './contribuyente';
import { Cuenta } from './cuenta';
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
    public anioAlta: string;
    public registroPatronal: string;
    public estatusJuridico: string;
    public estatusJuridico2: string;
    public representanteLegal: string;
    public ciec: string; //  Clave de Identificación Electrónica Confidencial (CIEC). Ahora llamada solo Contraseña del SAT
    public fiel: string; // FIEL (Firma Electrónica Avanzada), que es una contrasenia para timbrado

    // DOCUMENTOS
    //public logotipo: string;  se adjunta como documento
    //public llavePrivada: string; se adjunta como documento
    //public certificado: string; se adjunta como documento
    // Archivo CSD 
    // Acta constitutiva
    // comprobante domicilio empresa
    // INE representate legal

    // PASO 2 carga de datos digitales
    // datos digitales
    public web: string;
    public correo: string;
    public pwCorreo: string;
    public dominioCorreo: string;
    public pwSat: string; //sera reemplazada por la  FIEL (Firma Electrónica Avanzada), que es una contrasenia para timbrado
    
    
    
    // OPCIONAL, puede ser realizado despues de paso 1
    // contabilidad

    // FIEL
    // CIEC
    public actividadSAT: string;
    public noCertificado: string;
    public expiracionCertificado: Date;
    public expiracionFiel: Date;

    public creador: string;
    public fechaCreacion: Date;
    public fechaActualizacion: Date;

    constructor() {
        this.activo = false;
        this.estatus = 'INACTIVO';
        this.tipo= '*';
        this.giro='*';
        this.colonia = '*';
        this.anioAlta = '*';
        this.regimenFiscal = '*';
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