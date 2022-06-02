import { Observable } from 'rxjs';
import { GenericPage } from '../../models/generic-page';
import { Client } from '../../models/client';
import { ResourceFile } from '../../models/resource-file';

export abstract class ClientsData {

    abstract getClients(filterParams: any): Observable<GenericPage<Client>>;

    abstract getClientsReport(filterParams: any): Observable<ResourceFile>;
    
    abstract getClientsByPromotor(promotor: string): Observable<Client[]>;

    abstract getClientsByPromotorAndRfc(promotor: string,rfc:string): Observable<Client>;

    abstract getClientById(id: number): Observable<Client>;

    abstract getClientByName(name: string): Observable<Client[]>;

    abstract insertNewClient(client: Client): Observable<Client>;

    abstract updateClient(client: Client): Observable<Client>;
}
