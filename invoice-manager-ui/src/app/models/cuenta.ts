export class Cuenta {

    public id: string;
    public rfc: string;
    public banco: string;
    public cuenta: string;
    public clabe: string;
    public tipoContrato: string;
    public domicilioBanco: string;
    public sucursal: string;
    public expedienteActualizado: string;
    public linea: string;
    public razonSocial: string;
    // NO mapeado pero debe de existir una referencia de documetos como expediente, contrato, etc

    public fechaCreacion: Date;
    public fechaActualizacion: Date;
    constructor(id?: string, banco?: string, cuenta?: string) {
        this.id = id;
        this.banco = banco || '*';
        this.cuenta = cuenta;
        this.expedienteActualizado = 'NO';  
    }
}
