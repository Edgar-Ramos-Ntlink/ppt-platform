import { Component, OnInit, TemplateRef } from '@angular/core';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { Empresa } from '../../../models/empresa';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Client } from '../../../models/client';
import { NbDialogService } from '@nebular/theme';
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
import { Emisor } from '../../../@core/models/cfdi/emisor';
import { invoice } from '../../../@core/core.selectors';
import { ClientsData } from '../../../@core/data/clients-data';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

@Component({
    selector: 'ngx-linea-x',
    templateUrl: './linea-x.component.html',
    styleUrls: ['./linea-x.component.scss'],
})
export class LineaXComponent implements OnInit {
    public LINEAEMISOR: string = 'B';

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
        private dialogService: NbDialogService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.route.paramMap.subscribe((route) => {
            this.LINEAEMISOR = route.get('linea');
            const folio = route.get('folio');

            if (folio === '*') {
                this.loading = true;
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.clientsService
                    .getClients({page:0, size : 100000})
                    .subscribe(
                        (page) => {
                            this.clientsCat = page.content;
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
                    'Error'
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
                    .getCompaniesByLineaAndGiro(
                        this.LINEAEMISOR,
                        Number(giroId)
                    )
                    .toPromise();
            }
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
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
                `El cliente ${client.razonSocial} no se encuentra activo,notifique al supervisor para activarlo`,
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
            invoice.lineaEmisor = this.LINEAEMISOR;
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
                console.log('Sending invoice', invoice);
                invoice = await this.invoiceService
                    .insertNewInvoice(invoice)
                    .toPromise();
                this.loading = false;
                this.notificationService.sendNotification(
                    'success',
                    'Solicitud de factura enviada correctamente'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.router.navigate([
                    `./pages/operaciones/cfdi/${this.LINEAEMISOR}/${invoice.folio}`,
                ]);
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

    public linkInvoice(factura: Factura) {
        this.loading = true;
        const fact = { ...this.factura };
        this.invoiceService.generateReplacement(factura.folio, fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'El documento relacionado se ha generado exitosamente',
                    'Documento relacionado'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error en la sustitucion'
                );
                this.loading = false;
            }
        );
    }

    public generateCreditNoteInvoice(factura: Factura) {
        this.loading = true;
        const fact = { ...factura };
        this.invoiceService.generateCreditNote(factura.folio, fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'La nota de credito se ha generado exitosamente',
                    'Nota credito creada'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error creando nota de credito'
                );
                this.loading = false;
            }
        );
    }

    public aceptarFactura() {
        const fact: Factura = JSON.parse(JSON.stringify(this.factura));
        fact.validacionOper = true;
        fact.validacionTeso = true;
        fact.statusFactura = '4';
        fact.statusDetail = '';
        this.loading = true;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'factura aceptada'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error'
                );
                this.loading = false;
            }
        );
    }

    public async rechazarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact: Factura = JSON.parse(JSON.stringify(factura));
            fact.statusDetail = 'Campos inválidos en el CFDI';
            fact.notas = `Factura rechazada por operaciones : ${sessionStorage.getItem(
                'email'
            )}`;
            this.dialogService
                .open(dialog, { context: fact })
                .onClose.subscribe((result) => {
                    this.loading = true;
                    if (result !== undefined) {
                        result.statusFactura = '6'; // update to rechazo operaciones
                        this.invoiceService.updateInvoice(result).subscribe(
                            (invoice) => {
                                this.notificationService.sendNotification(
                                    'success',
                                    'factura rechazada'
                                );
                                this.store.dispatch(updateInvoice({ invoice }));
                                this.loading = false;
                            },
                            (error: NtError) => {
                                this.notificationService.sendNotification(
                                    'danger',
                                    error?.message,
                                    'Error'
                                );
                                this.loading = false;
                            }
                        );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
            this.loading = false;
        }
    }

    public async timbrarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact = { ...factura };
                this.dialogService
                    .open(dialog, { context: fact })
                    .onClose.subscribe((invoice) => {
                        this.loading = true;
                        if (invoice !== undefined) {
                            this.invoiceService
                                .timbrarFactura(fact.folio, invoice)
                                .subscribe(
                                    (invoice) => {
                                        this.notificationService.sendNotification(
                                            'success',
                                            'factura timbrada'
                                        );
                                        this.store.dispatch(
                                            updateInvoice({ invoice })
                                        );
                                        this.loading = false;
                                    },
                                    (error: NtError) => {
                                        this.notificationService.sendNotification(
                                            'danger',
                                            error?.message,
                                            'Error'
                                        );
                                        this.loading = false;
                                    }
                                );
                        } else {
                            this.loading = false;
                        }
                    });
            
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
            this.loading = false;
        }
    }

    public async cancelarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact = JSON.parse(JSON.stringify(factura));
            fact.motivo = '02';
            this.dialogService
                .open(dialog, { context: fact })
                .onClose.subscribe((result) => {
                    this.loading = true;
                    if (result !== undefined) {
                        this.invoiceService
                            .cancelarFactura(fact.folio, result)
                            .subscribe(
                                (invoice) => {
                                    this.notificationService.sendNotification(
                                        'success',
                                        'factura cancelada'
                                    );
                                    this.store.dispatch(
                                        updateInvoice({ invoice })
                                    );
                                    this.loading = false;
                                },
                                (error: NtError) => {
                                    this.notificationService.sendNotification(
                                        'danger',
                                        error?.message,
                                        'Error'
                                    );
                                    this.loading = false;
                                }
                            );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
            this.loading = false;
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
                    'actualización exitosa',
                    'CFDI Revalidado'
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error en la ravalidacion'
                );
            }
        );
    }
}
