export class SupportRequest {
    public folio: number;
    public contactPhone: string;
    public contactEmail: string;
    public contactName: string;
    public status: string;
    public supportType: string;
    public agent: string;
    public module: string;
    public supportLevel: string;
    public requestType: string;
    public problem: string;
    public errorMessage: string;
    public solution: string;
    public notes: string;
    public dueDate: Date;
    public creation: Date;
    public update: Date;

    constructor(contactEmail?: string) {
        this.status = 'PENDIENTE';
        this.supportType = '*';
        this.agent = 'soporte@ntlink.com.mx';
        this.contactEmail = contactEmail;
        this.supportLevel = 'primer nivel';
        this.requestType = '*';
        this.problem = '';
        this.errorMessage = '';
        this.notes = '';
        this.module = '*';
        this.solution = '';
        const duedate = new Date();
        duedate.setDate(duedate.getDate() + 1);
        this.dueDate = duedate;
    }
}
