export class Client {

    public id: number;
    public activo: boolean;
    public moral: boolean;
    public notas: string;
    public porcentajePromotor: number;
    public porcentajeCliente: number;
    public porcentajeDespacho: number;
    public porcentajeContacto: number;
    public correoPromotor: string;
    public correoContacto: string;
    
    public rfc: string;
    public giro: string;
    public nombre: string;
    public razonSocial: string;
    public calle: string;
    public noInterior: number;
    public noExterior: number;
    public municipio: string;
    public regimenFiscal: string;
    public estado: string;
    public pais: string;
    public localidad: string;
    public cp: string;
    public fechaCreacion: Date;
    public fechaActualizacion: Date;
    


    constructor(rfc?:string,nombre?:string) {
        this.porcentajePromotor = 0;
        this.porcentajeCliente = 0;
        this.porcentajeDespacho = 0;
        this.porcentajeContacto = 0;
        this.correoContacto = '';
        this.regimenFiscal = '*';
        this.activo = false;
        this.moral = false;
        this.rfc = rfc;
        this.razonSocial = nombre;
    }

}
