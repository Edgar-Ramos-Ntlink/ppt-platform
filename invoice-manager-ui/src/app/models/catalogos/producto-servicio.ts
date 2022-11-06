export class ClaveProductoServicio{

    public clave : string;
	public descripcion:string;
	public similares : string;
	public inicioVigencia : Date;

	constructor(clave?:string,descripcion?:string){
		this.clave = clave;
		this.descripcion = descripcion;
	}
}