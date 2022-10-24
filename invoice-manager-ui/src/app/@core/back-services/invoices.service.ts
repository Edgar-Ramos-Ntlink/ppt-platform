import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Catalogo } from '../../models/catalogos/catalogo';
import { CatalogsData } from '../data/catalogs-data';
import { map } from 'rxjs/operators';
import { GenericPage } from '../../models/generic-page';
import { Pago } from '../models/cfdi/pago';
import { Factura } from '../models/factura';

@Injectable({
    providedIn: 'root',
})
export class InvoicesService {
    private validationCat: Catalogo[] = [];
    private payCat: Catalogo[] = [];
    private formaPagoCat: Catalogo[] = [];

    constructor(
        private httpClient: HttpClient,
        private catalogService: CatalogsData
    ) {
        this.catalogService.getStatusPago().then((cat) => (this.payCat = cat));
        this.catalogService
            .getStatusValidacion()
            .then((cat) => (this.validationCat = cat));
        this.catalogService
            .getFormasPago()
            .then((cat) => (this.formaPagoCat = cat));
    }

    private getHttpParams(filterParams: any): HttpParams {
        let pageParams: HttpParams = new HttpParams();
        for (const key in filterParams) {
            if (filterParams[key] !== undefined) {
                const value: string = filterParams[key].toString();
                if (value !== null && value.length > 0 && value !== '*') {
                    pageParams = pageParams.append(key, value);
                }
            }
        }
        return pageParams;
    }

    public getInvoices(filterParams: any): Observable<any> {
        return this.httpClient.get('../api/facturas', {
            params: this.getHttpParams(filterParams),
        });
    }

    public getInvoicesReports(filterParams: any): Observable<any> {
        return this.httpClient.get('../api/facturas/factura-reports', {
            params: this.getHttpParams(filterParams),
        });
    }
    public getComplementReports(filterParams: any): Observable<any> {
        return this.httpClient.get('../api/facturas/complemento-reports', {
            params: this.getHttpParams(filterParams),
        });
    }

    public getInvoiceByFolio(folio: string): Observable<any> {
        return this.httpClient.get(`../api/facturas/${folio}`);
    }

    public getInvoiceFiles(folio: string): Observable<any> {
        return this.httpClient.get(`../api/facturas/${folio}/files`);
    }

    public getComplementosInvoice(folioPadre: string): Observable<any> {
        return this.httpClient.get(
            `../api/facturas/${folioPadre}/complementos`
        );
    }

    public timbrarFactura(folio: string, factura: Factura): Observable<any> {
        return this.httpClient.post(
            `../api/facturas/${folio}/timbrar`,
            factura
        );
    }

    public cancelarFactura(folio: string, factura: Factura): Observable<any> {
        return this.httpClient.post(
            `../api/facturas/${folio}/cancelar`,
            factura
        );
    }

    public insertNewInvoice(invoice: Factura): Observable<any> {
        return this.httpClient.post('../api/facturas', invoice);
    }

    public updateInvoice(invoice: Factura): Observable<any> {
        return this.httpClient.put(
            `../api/facturas/${invoice.cfdi.folio}`,
            invoice
        );
    }

    public generateInvoiceComplement(
        folioPadre: string,
        complemento: Pago
    ): Observable<any> {
        return this.httpClient.post(
            `../api/facturas/${folioPadre}/complementos`,
            complemento
        );
    }

    public generateReplacement(
        folioFact: string,
        factura: Factura
    ): Observable<any> {
        return this.httpClient.post(
            `../api/facturas/${folioFact}/sustitucion`,
            factura
        );
    }

    public generateCreditNote(
        folioFact: string,
        factura: Factura
    ): Observable<any> {
        return this.httpClient.post(
            `../api/facturas/${folioFact}/nota-credito`,
            factura
        );
    }

    public getInvoiceSaldo(folio: string): Observable<any> {
        return this.httpClient.get(`../api/facturas/${folio}/saldos`);
    }

    public reSendEmail(folio: string): Observable<any> {
        return this.httpClient.post(`../api/facturas/${folio}/correos`, folio);
    }

    public rebuildPdf(folio:string): Observable<any> {
        return this.httpClient.post(`../api/facturas/${folio}/reconstruccion-pdf`, folio);
    }
}
