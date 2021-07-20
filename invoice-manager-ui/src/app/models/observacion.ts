export class Observacion {

    public id: number;
    public rfc: string;
    public area: string;
    public observacion: string;
    public detalles: string;
    public notificante: string;


    constructor(id: number, rfc: string, area: string, observacion: string, detalles: string, notificante: string) {
        this.id = id;
        this.area = area;
        this. observacion = observacion;
        this. detalles = detalles;
        this. notificante = notificante;
        this.rfc = rfc;
	}

}