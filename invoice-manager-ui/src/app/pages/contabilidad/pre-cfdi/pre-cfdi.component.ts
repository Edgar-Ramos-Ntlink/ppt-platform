import { Component, OnInit, TemplateRef } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { Empresa } from '../../../models/empresa';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { PagoBase } from '../../../models/pago-base';
import { ActivatedRoute, Router } from '@angular/router';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { PaymentsData } from '../../../@core/data/payments-data';
import { Client } from '../../../models/client';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { NtError } from '../../../@core/models/nt-error';
import { AppState } from '../../../reducers';
import { select, Store } from '@ngrx/store';
import {
    initInvoice,
    updateInvoice,
    updateReceptor,
    updateReceptorAddress,
} from '../../../@core/core.actions';
import { invoice } from '../../../@core/core.selectors';
import { Emisor } from '../../../@core/models/cfdi/emisor';
import { Receptor } from '../../../@core/models/cfdi/receptor';
import { ClientsData } from '../../../@core/data/clients-data';
import { GenericPage } from '../../../models/generic-page';
import { map } from 'rxjs/operators';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

@Component({
    selector: 'ngx-pre-cfdi',
    templateUrl: './pre-cfdi.component.html',
    styleUrls: ['./pre-cfdi.component.scss'],
})
export class PreCfdiComponent implements OnInit {
    public girosCat: Catalogo[] = [];
    public emisoresCat: Empresa[] = [];
    public receptoresCat: Empresa[] = [];
    public clientsCat: Client[] = [];
    public payCat: Catalogo[] = [];
    public payTypeCat: Catalogo[] = [
        new Catalogo('01', 'Efectivo'),
        new Catalogo('02', 'Cheque nominativo'),
        new Catalogo('03', 'Transferencia electrónica de fondos'),
        new Catalogo('99', 'Por definir'),
    ];

    public complementPayTypeCat: Catalogo[] = [];

    public payment: Pago;
    public factura: Factura;
    public folioParam: string;
    public isSupport: boolean = false;
    public folio: string;
    public json: string;
    public formInfo = {
        emisorRfc: '*',
        receptorRfc: '*',
        giroReceptor: '*',
        giroEmisor: '*',
        lineaEmisor: 'B',
        lineaReceptor: 'A',
        usoCfdi: '*',
        payType: '*',
        clientRfc: '*',
        clientName: '',
        companyRfc: '',
        giro: '*',
        empresa: '*',
    };

    public loading: boolean = false;

    /** PAYMENT SECCTION**/

    public paymentForm = {
        coin: '*',
        payType: '*',
        bank: '*',
        filename: '',
        successPayment: false,
    };
    public newPayment: PagoBase;
    public invoicePayments = [];
    public paymentSum: number = 0;

    //
    public pagosCfdi: Pago[] = [];

    public companiesCat: Empresa[] = [];

    constructor(
        private catalogsService: CatalogsData,
        private companiesService: CompaniesData,
        private invoiceService: InvoicesData,
        private paymentsService: PaymentsData,
        private clientsService: ClientsData,
        private cfdiValidator: CfdiValidatorService,
        private notificationService: NotificationsService,
        private route: ActivatedRoute,
        private router: Router,
        private dialogService: NbDialogService,
        private store: Store<AppState>
    ) {}

    ngOnDestroy() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    ngOnInit() {
        this.loading = true;
        this.paymentsService
            .getFormasPago()
            .subscribe((payTypes) => (this.complementPayTypeCat = payTypes));
        /* preloaded cats*/
        this.catalogsService.getStatusPago().then((cat) => (this.payCat = cat));
        this.catalogsService
            .getFormasPago()
            .then((cat) => (this.payTypeCat = cat));
        this.catalogsService
            .getAllGiros()
            .then((cat) => (this.girosCat = cat))
            .then(() => {
                this.route.paramMap.subscribe((route) => {
                    const folio = route.get('folio');
                    if (folio === '*') {
                        this.store.dispatch(
                            initInvoice({ invoice: new Factura() })
                        );
                        this.loading = false;
                    } else {
                        this.getInvoiceByFolio(folio);
                    }
                    this.folio = folio;
                });
            });
        const user = JSON.parse(sessionStorage.getItem('user'));
        this.isSupport =
            user.roles.find((u) => u.role == 'ADMINISTRADOR') != undefined;

        this.store.pipe(select(invoice)).subscribe((fact) => {
            this.factura = fact;
            this.json = JSON.stringify(this.factura, null, '\t');
        });
    }

    public getInvoiceByFolio(folio: string) {
        this.pagosCfdi = [];
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error'
                );
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.loading = false;
            }
        );
    }

    onGiroEmisorSelection(giroId: string) {
        const value = +giroId;
        if (isNaN(value)) {
            this.emisoresCat = [];
        } else {
            this.companiesService
                .getCompaniesByLineaAndGiro(
                    this.formInfo.lineaEmisor,
                    Number(giroId)
                )
                .subscribe(
                    (companies) => (this.emisoresCat = companies),
                    (error: NtError) =>
                        this.notificationService.sendNotification(
                            'danger',
                            error?.message,
                            'Error recuperando emisores'
                        )
                );
        }
    }

    onGiroReceptorSelection(giroId: string) {
        const value = +giroId;
        if (isNaN(value)) {
            this.receptoresCat = [];
        } else {
            this.companiesService
                .getCompaniesByLineaAndGiro(
                    this.formInfo.lineaReceptor,
                    Number(giroId)
                )
                .subscribe(
                    (companies) => (this.receptoresCat = companies),
                    (error: NtError) => (error: NtError) =>
                        this.notificationService.sendNotification(
                            'danger',
                            error?.message,
                            'Error recuperando receptores'
                        )
                );
        }
    }

    public onEmisorSelected(rfc: string) {
        const companyInfo = this.emisoresCat.find((c) => c.rfc === rfc);

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

    public onReceptorSelected(rfc: string) {
        const clientInfo: Empresa = this.receptoresCat.find(
            (c) => c.rfc === rfc
        );

        if (!clientInfo.activo) {
            this.notificationService.sendNotification(
                'warning',
                `El receptor ${clientInfo.razonSocial} no se encuentra activo,notifique al supervisor para activarlo`,
                'Cliente inactivo'
            );
            return;
        }

        if (
            clientInfo.regimenFiscal == undefined ||
            clientInfo.regimenFiscal == null ||
            clientInfo.regimenFiscal === '*'
        ) {
            this.notificationService.sendNotification(
                'warning',
                `El receptor ${clientInfo.razonSocial} no cuenta con regimen fiscal, delo de alta antes de continuar`,
                'Informacion faltante'
            );
            return;
        }
        let receptor = new Receptor();
        receptor.rfc = clientInfo.rfc.toUpperCase();
        receptor.nombre = clientInfo.nombre.toUpperCase();
        receptor.domicilioFiscalReceptor = clientInfo.cp;
        receptor.regimenFiscalReceptor = clientInfo.regimenFiscal;

        let address = this.cfdiValidator.generateAddress(clientInfo);
        this.store.dispatch(updateReceptor({ receptor }));
        this.store.dispatch(updateReceptorAddress({ address }));
    }

    public onClientSelected(rfc: string) {
        const client = this.clientsCat.find((c) => rfc === c.rfc);

        if (!client.activo) {
            this.notificationService.sendNotification(
                'warning',
                `El cliente ${client.razonSocial} no se encuentra activo,notifique al supervisor para activarlo`,
                'Cliente inactivo'
            );
            return;
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
            return;
        }
        let receptor = new Receptor();
        receptor.rfc = client.rfc.toUpperCase();
        receptor.nombre = client.razonSocial.toUpperCase();
        receptor.domicilioFiscalReceptor = client.cp;
        receptor.regimenFiscalReceptor = client.regimenFiscal;

        let address = this.cfdiValidator.generateAddress(client);
        this.store.dispatch(updateReceptor({ receptor }));
        this.store.dispatch(updateReceptorAddress({ address }));
    }

    buscarClientInfo(razonSocial: string) {
        if (razonSocial !== undefined && razonSocial.length > 5) {
            this.clientsService
                .getClients({ razonSocial: razonSocial, page: '0', size: '20' })
                .pipe(map((page: GenericPage<Client>) => page.content))
                .subscribe(
                    (clients) => {
                        this.clientsCat = clients;
                    },
                    (error: NtError) => {
                        this.loading = false;
                        this.notificationService.sendNotification(
                            'danger',
                            error.message,
                            'Error'
                        );
                    }
                );
        } else {
            this.clientsCat = [];
        }
    }

    onPayMethodSelected(clave: string) {
        if (clave === 'PPD') {
            this.payTypeCat = [new Catalogo('99', 'Por definir')];
            this.factura.cfdi.formaPago = '99';
            this.factura.cfdi.metodoPago = 'PPD';
            this.factura.metodoPago = 'PPD';
            this.formInfo.payType = '99';
        } else {
            this.payTypeCat = [
                new Catalogo('02', 'Cheque nominativo'),
                new Catalogo('03', 'Transferencia electrónica de fondos'),
            ];
            this.factura.metodoPago = 'PUE';
            this.factura.cfdi.metodoPago = 'PUE';
            this.factura.cfdi.formaPago = '03';
            this.formInfo.payType = '03';
        }
    }

    onUsoCfdiSelected(clave: string) {
        this.factura.cfdi.receptor.usoCfdi = clave;
    }

    onFormaDePagoSelected(clave: string) {
        this.factura.cfdi.formaPago = clave;
    }

    limpiarForma() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    public async solicitarCfdi() {
        this.loading = true;
        try {
            let invoice = JSON.parse(JSON.stringify(this.factura));
            invoice.solicitante = sessionStorage.getItem('email');
            invoice.lineaEmisor = this.formInfo.lineaEmisor || 'B';
            invoice.lineaRemitente = this.formInfo.lineaReceptor || 'A';
            invoice.metodoPago = invoice.cfdi.metodoPago;
            invoice.formaPago = invoice.cfdi.formaPago;
            invoice.rfcEmisor = invoice.cfdi.emisor.rfc;
            invoice.razonSocialEmisor = invoice.cfdi.emisor.nombre;
            invoice.rfcRemitente = invoice.cfdi.receptor.rfc;
            invoice.razonSocialRemitente = invoice.cfdi.receptor.nombre;
            invoice.statusFactura = '8';

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
                this.router.navigate([
                    `./pages/contabilidad/cfdi/${invoice.folio}`,
                ]);
            } else {
                errors.forEach((e) =>
                    this.notificationService.sendNotification(
                        'warning',
                        e,
                        'Informacion incompleta'
                    )
                );
                this.loading = false;
            }
        } catch (error) {
            this.loading = false;
            this.notificationService.sendNotification(
                'danger',
                error.message,
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
                    error.message,
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
                    error.message,
                    'Error en la creacion de la nota de credito'
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
                        result.statusFactura = '9'; // update to rechazo contabilidad
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
                error.message,
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
                                        'Error en el timbrado'
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
        fact.statusFactura = '8';
        fact.validacionOper = false;
        fact.total = this.factura.cfdi.total;
        fact.metodoPago = this.factura.cfdi.metodoPago;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.loading = false;
                this.store.dispatch(updateInvoice({ invoice }));
                this.notificationService.sendNotification(
                    'success',
                    'acctualización exitosa',
                    'CFDI Revalidado'
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error en la revalidacion'
                );
            }
        );
    }

    public rebuildPdf() {
        this.invoiceService.rebuildPdf(this.factura.folio).subscribe(
            () =>
                this.notificationService.sendNotification(
                    'success',
                    'PDF reconstuido'
                ),
            (error: NtError) =>
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error en la recostruccion del PDF'
                )
        );
    }

    public async jsonUpdate(dialog: TemplateRef<any>) {
        try {
            this.dialogService.open(dialog).onClose.subscribe((result) => {
                this.loading = true;
                if (result !== undefined) {
                    const fact = JSON.parse(this.json);
                    console.log('Updating with JSON:', fact);
                    this.invoiceService.updateInvoice(fact).subscribe(
                        (invoice) => {
                            this.notificationService.sendNotification(
                                'success',
                                'factura Actualizada'
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

    onGiroSelection(giroId: string) {
        const value = +giroId;
        if (isNaN(value)) {
            this.emisoresCat = [];
        } else {
            this.companiesService
                .getCompaniesByLineaAndGiro(
                    this.formInfo.lineaEmisor,
                    Number(giroId)
                )
                .subscribe(
                    (companies) => (this.emisoresCat = companies),
                    (error: NtError) => {
                        this.loading = false;
                        this.notificationService.sendNotification(
                            'danger',
                            error?.message,
                            'Error'
                        );
                    }
                );
        }
    }
}
