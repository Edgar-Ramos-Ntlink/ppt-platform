
export class DetalleEmpresa {

    public id: number;
    public rfc: string;
    public tipo: string;
    public area: string;
    public resumen: string;
    public detalle: string;
    public notificante: string;


    constructor(rfc?:string, area?: string, notificate ?:string, tipo?: string) {
        this.rfc = rfc ;
        this.area = area;
        this.notificante = notificate;
        this.tipo = tipo;
        this.resumen = '';
        this.detalle = '';
	}

}