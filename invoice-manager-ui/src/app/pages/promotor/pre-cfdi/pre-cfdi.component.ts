import { Component, OnInit, OnDestroy } from '@angular/core';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ClientsData } from '../../../@core/data/clients-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { Empresa } from '../../../models/empresa';
import { Client } from '../../../models/client';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { ActivatedRoute, Router } from '@angular/router';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import {
    initInvoice,
    updateInvoice,
    updateReceptor,
    updateReceptorAddress,
} from '../../../@core/core.actions';
import { NtError } from '../../../@core/models/nt-error';
import { Receptor } from '../../../@core/models/cfdi/receptor';
import { invoice } from '../../../@core/core.selectors';
import { Emisor } from '../../../@core/models/cfdi/emisor';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

@Component({
    selector: 'nt-pre-cfdi',
    templateUrl: './pre-cfdi.component.html',
    styleUrls: ['../../pages.component.scss'],
})
export class PreCfdiComponent implements OnInit, OnDestroy {
    public folio: string;
    public pagosCfdi: Pago[] = [];
    public girosCat: Catalogo[] = [];
    public companiesCat: Empresa[] = [];
    public clientsCat: Client[] = [];
    public formInfo: any = { giro: '*', empresa: '*' };

    public factura: Factura = new Factura();
    public loading: boolean = false;

    constructor(
        private catalogsService: CatalogsData,
        private clientsService: ClientsData,
        private companiesService: CompaniesData,
        private invoiceService: InvoicesData,
        private cfdiValidator: CfdiValidatorService,
        private notificationService: NotificationsService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.route.paramMap.subscribe((route) => {
            const folio = route.get('folio');

            if (folio === '*') {
                this.loading = true;
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.clientsService
                    .getClientsByPromotor(sessionStorage.getItem('email'))
                    .subscribe(
                        (clients) => {
                            this.clientsCat = clients;
                            this.loading = false;
                        },
                        (error) => (this.loading = false)
                    );
            } else {
                this.getInvoiceByFolio(folio);
            }
            this.folio = folio;
        });

        this.catalogsService.getAllGiros().then((cat) => (this.girosCat = cat));
        this.store
            .pipe(select(invoice))
            .subscribe((fact) => (this.factura = fact));
    }

    ngOnDestroy() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    public getInvoiceByFolio(folio: string) {
        this.pagosCfdi = [];
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error recuperando informacion CFDI'
                );
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.loading = false;
            }
        );
    }

    public async onGiroSelection(giroId: string) {
        try {
            const value = +giroId;
            if (isNaN(value)) {
                this.companiesCat = [];
            } else {
                this.companiesCat = await this.companiesService
                    .getCompaniesByLineaAndGiro('A', Number(giroId))
                    .toPromise();
            }
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error giro empresas'
            );
        }
    }

    onCompanySelected(companyId: string) {
        const companyInfo = this.companiesCat.find(
            (c) => c.id === Number(companyId)
        );

        const emisor = new Emisor();
        emisor.rfc = companyInfo.rfc.toUpperCase();
        emisor.nombre = companyInfo.nombre.toUpperCase();
        emisor.regimenFiscal = companyInfo.regimenFiscal || '*';

        let invoice = JSON.parse(JSON.stringify(this.factura));
        invoice.cfdi.emisor = emisor;
        invoice.rfcEmisor = emisor.rfc;
        invoice.razonSocialEmisor = emisor.nombre;
        invoice.lineaEmisor = companyInfo.tipo;
        invoice.lineaRemitente = 'CLIENTE';
        invoice.cfdi.lugarExpedicion = companyInfo.cp;
        invoice.direccionEmisor =
            this.cfdiValidator.generateAddress(companyInfo);

        this.store.dispatch(updateInvoice({ invoice }));
    }

    public onClientSelected(client: Client) {
        if (!client.activo) {
            this.notificationService.sendNotification(
                'warning',
                `El cliente ${client.razonSocial} no se encuentra activo, solicite su activación al equipo de operaciones`,
                'Cliente inactivo'
            );
        }

        if (
            client.regimenFiscal == undefined ||
            client.regimenFiscal == null ||
            client.regimenFiscal === '*'
        ) {
            this.notificationService.sendNotification(
                'warning',
                `El cliente ${client.razonSocial} no cuenta con regimen fiscal, delo de alta antes de continuar`,
                'Informacion faltante'
            );
        }
        let receptor = new Receptor();
        receptor.rfc = client.rfc.toUpperCase();
        receptor.nombre = client.razonSocial.toUpperCase();
        receptor.domicilioFiscalReceptor = client.cp || '00000';
        receptor.regimenFiscalReceptor = client.regimenFiscal || '*';

        let address = this.cfdiValidator.generateAddress(client);
        this.store.dispatch(updateReceptor({ receptor }));
        this.store.dispatch(updateReceptorAddress({ address }));
    }

    limpiarForma() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    public async solicitarCfdi() {
        this.loading = true;
        try {
            let invoice = JSON.parse(JSON.stringify(this.factura));
            invoice.solicitante = sessionStorage.getItem('email');
            invoice.lineaEmisor = 'A';
            invoice.lineaRemitente = 'CLIENTE';
            invoice.metodoPago = invoice.cfdi.metodoPago;
            invoice.formaPago = invoice.cfdi.formaPago;
            invoice.rfcEmisor = invoice.cfdi.emisor.rfc;
            invoice.razonSocialEmisor = invoice.cfdi.emisor.nombre;
            invoice.rfcRemitente = invoice.cfdi.receptor.rfc;
            invoice.razonSocialRemitente = invoice.cfdi.receptor.nombre;
            invoice.statusFactura = '1';

            let errors: string[] = this.cfdiValidator.validarCfdi(invoice.cfdi);
            if (errors.length === 0) {
                invoice = await this.invoiceService
                    .insertNewInvoice(invoice)
                    .toPromise();
                this.loading = false;
                this.notificationService.sendNotification(
                    'success',
                    'Solicitud de factura enviada correctamente'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.router.navigate([`./pages/promotor/precfdi/${invoice.folio}`]);
            } else {
                errors.forEach((e) =>
                    this.notificationService.sendNotification(
                        'warning',
                        e,
                        'Información incompleta'
                    )
                );
                this.loading = false;
            }
        } catch (error) {
            this.loading = false;
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
        }
    }

    public async revalidateInvoice() {
        this.loading = true;
        const fact: Factura = JSON.parse(JSON.stringify(this.factura));
        fact.statusFactura = '1';
        fact.validacionOper = false;
        fact.total = this.factura.cfdi.total;
        fact.metodoPago = this.factura.cfdi.metodoPago;

        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.loading = false;
                this.store.dispatch(updateInvoice({ invoice }));
                this.notificationService.sendNotification(
                    'success',
                    'CFDI revalidado correctamente'
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error en revalidacion'
                );
            }
        );
    }

    public returnToSourceFact(folio: number) {
        this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
    }

    public goToRelacionado(folio: number) {
        this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
    }
}
