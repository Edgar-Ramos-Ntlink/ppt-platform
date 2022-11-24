export class SupportRequest {
    public folio: number;
    public clientId: number;
    public clientEmail: string;
    public companyRfc: string;
    public companyName: string;
    public contactPhone: string;
    public contactEmail: string;
    public contactName: string;
    public product: string;
    public status: string;
    public supportType: string;
    public agent: string;
    public supportLevel: string;
    public requestType: string;
    public problem: string;
    public solution: string;
    public notes: string;
    public dueDate: Date;
    public creation: Date;
    public update: Date;
    public module: string;

    constructor(clientEmail?: string) {
        this.status = 'PENDIENTE';
        this.supportType = '*';
        this.agent = 'soporte@ntlink.com.mx';
        this.clientEmail = clientEmail;
        this.supportLevel = 'primer nivel';
        this.requestType = '*';
        this.problem = '';
        this.notes = '*';
        this.solution = '';
        this.product = 'SJ INVOICE MANAGER';
        const duedate = new Date();
        duedate.setDate(duedate.getDate() + 1);
        this.dueDate = duedate;
    }
}
