export class ComplementoPago {
    public idDocumento: string;
    public folioOrigen: string;
    public folio: string;
    public equivalenciaDR: string;
    public monedaDr: string;
    public fechaPago: Date;
    public numeroParcialidad: number;
    public importeSaldoAnterior: number;
    public importePagado: number;
    public importeSaldoInsoluto: number;
    public valido: boolean;
    public tipoCambio: number;
}
