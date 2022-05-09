import { Component, OnInit, TemplateRef } from '@angular/core';
import { NbDialogService, NbToastrService } from '@nebular/theme';
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
import { CfdiData } from '../../../@core/data/cfdi-data';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { NtError } from '../../../@core/models/nt-error';
import { AppState } from '../../../reducers';
import { select, Store } from '@ngrx/store';
import { initInvoice, updateInvoice } from '../../../@core/core.actions';
import { invoice } from '../../../@core/core.selectors';
import { AppConstants } from '../../../models/app-constants';

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
    public soporte: boolean = false;
    public folio: string;

    public complementos: Factura[] = [];

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

    public clientInfo: Empresa;
    public companyInfo: Empresa;

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
        private cfdiService: CfdiData,
        private cfdiValidator: CfdiValidatorService,
        private toastrService: NbToastrService,
        private route: ActivatedRoute,
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

        this.store
            .pipe(select(invoice))
            .subscribe((fact) => (this.factura = fact));
    }

    public getInvoiceByFolio(folio: string) {
        this.pagosCfdi = [];
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
                if (
                    invoice.metodoPago === 'PPD' &&
                    invoice.cfdi.tipoDeComprobante === 'P'
                ) {
                    alert('Implement pagos logic');
                    //this.pagosCfdi = cfdi.complemento[0].pagos;
                }
                if (
                    invoice.metodoPago === 'PPD' &&
                    invoice.tipoDocumento === 'Factura'
                ) {
                    this.cfdiService
                        .findInvoicePaymentComplementsByFolio(folio)
                        .subscribe((pagos) => (this.pagosCfdi = pagos));
                }
                this.loading = false;
            },
            (error: NtError) => {
                this.toastrService.danger(error.message);
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
                        this.toastrService.danger(
                            error?.message,
                            'Error recuperando emisores',
                            AppConstants.TOAST_CONFIG
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
                        this.toastrService.danger(
                            error?.message,
                            'Error recuperando receptores',
                            AppConstants.TOAST_CONFIG
                        )
                );
        }
    }

    onEmnisorSelected(companyId: string) {
        this.companyInfo = this.emisoresCat.find(
            (c) => c.id === Number(companyId)
        );
        this.factura.rfcEmisor = this.companyInfo.rfc;
        this.factura.razonSocialEmisor =
            this.companyInfo.razonSocial.toUpperCase();
        this.factura.cfdi.emisor.regimenFiscal = this.companyInfo.regimenFiscal;
        this.factura.cfdi.emisor.rfc = this.companyInfo.rfc;
        this.factura.cfdi.emisor.nombre =
            this.companyInfo.razonSocial.toUpperCase();
        this.factura.cfdi.lugarExpedicion = this.companyInfo.cp;
        this.factura.direccionEmisor =
            this.cfdiValidator.generateCompanyAddress(this.companyInfo);
    }

    onReceptorSelected(companyId: string) {
        this.clientInfo = this.receptoresCat.find(
            (c) => c.id === Number(companyId)
        );
        this.factura.rfcRemitente = this.clientInfo.rfc;
        this.factura.razonSocialRemitente =
            this.clientInfo.razonSocial.toUpperCase();
        this.factura.cfdi.receptor.rfc = this.clientInfo.rfc;
        this.factura.cfdi.receptor.nombre =
            this.clientInfo.razonSocial.toUpperCase();
        this.factura.direccionReceptor =
            this.cfdiValidator.generateCompanyAddress(this.clientInfo);
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

    public solicitarCfdi() {
        this.loading = true;
        const fact = JSON.parse(JSON.stringify(this.factura));
        let errorMessages: string[] = [];
        fact.solicitante = sessionStorage.getItem('email');
        if (
            this.clientInfo === undefined ||
            this.clientInfo.rfc === undefined
        ) {
            errorMessages.push(
                'La informacion del cliente es insuficiente o no esta presente.'
            );
        } else if (this.companyInfo === undefined) {
            errorMessages.push(
                'La informacion de la empresa es insuficiente o no esta presente.'
            );
        } else {
            fact.rfcEmisor = this.companyInfo.rfc;
            fact.razonSocialEmisor = this.companyInfo.razonSocial;
            fact.cfdi.emisor.regimenFiscal = this.companyInfo.regimenFiscal;
            fact.cfdi.emisor.rfc = this.companyInfo.rfc;
            fact.cfdi.emisor.nombre = this.companyInfo.razonSocial;
            fact.rfcEmisor = this.companyInfo.rfc;
            fact.razonSocialEmisor = this.companyInfo.razonSocial;

            fact.rfcRemitente = this.clientInfo.rfc;
            fact.razonSocialRemitente = this.clientInfo.razonSocial;
            fact.cfdi.receptor.rfc = this.clientInfo.rfc;
            fact.cfdi.receptor.nombre = this.clientInfo.razonSocial;
            fact.direccionEmisor = this.cfdiValidator.generateCompanyAddress(
                this.companyInfo
            );
            fact.direccionReceptor = this.cfdiValidator.generateCompanyAddress(
                this.clientInfo
            );

            fact.lineaEmisor = this.formInfo.lineaEmisor || 'B';
            fact.lineaRemitente = this.formInfo.lineaReceptor || 'A';
            fact.metodoPago = fact.cfdi.metodoPago;
            fact.statusFactura = '8'; // sets automatically to stamp directly
            errorMessages = this.cfdiValidator.validarCfdi({
                ...fact.cfdi,
            });
        }
        if (errorMessages.length === 0) {
            this.invoiceService.insertNewInvoice(this.factura).subscribe(
                (invoice: Factura) => {
                    this.factura.folio = invoice.folio;
                    this.getInvoiceByFolio(invoice.folio);
                    this.loading = false;
                },
                (error: NtError) => {
                    this.loading = false;
                    this.toastrService.danger(
                        error?.message,
                        'Error',
                        AppConstants.TOAST_CONFIG
                    );
                }
            );
        } else {
            this.loading = false;
        }
    }

    public linkInvoice(factura: Factura) {
        this.loading = true;
        const fact = { ...this.factura };
        this.invoiceService.generateReplacement(factura.folio, fact).subscribe(
            (invoice) => {
                this.toastrService.success(
                    'El documento relacionado se ha generado exitosamente',
                    'Documento relacionado',
                    AppConstants.TOAST_CONFIG
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.toastrService.danger(
                    error?.message,
                    'Error en la sustitución',
                    AppConstants.TOAST_CONFIG
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
                this.toastrService.success(
                    'La nota de credito se ha generado exitosamente',
                    'Nota credito creada',
                    AppConstants.TOAST_CONFIG
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.toastrService.danger(
                    error?.message,
                    'Error creando la nota de crédito',
                    AppConstants.TOAST_CONFIG
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
                                this.toastrService.success(
                                    '',
                                    'factura rechazada',
                                    AppConstants.TOAST_CONFIG
                                );
                                this.store.dispatch(updateInvoice({ invoice }));
                                this.loading = false;
                            },
                            (error: NtError) => {
                                this.toastrService.danger(
                                    error?.message,
                                    'Error',
                                    AppConstants.TOAST_CONFIG
                                );
                                this.loading = false;
                            }
                        );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.toastrService.danger(
                error?.message,
                'Error',
                AppConstants.TOAST_CONFIG
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
                                    this.toastrService.success(
                                        '',
                                        'factura timbrada',
                                        AppConstants.TOAST_CONFIG
                                    );
                                    this.store.dispatch(
                                        updateInvoice({ invoice })
                                    );
                                    this.loading = false;
                                },
                                (error: NtError) => {
                                    this.toastrService.danger(
                                        error?.message,
                                        'Error',
                                        AppConstants.TOAST_CONFIG
                                    );
                                    this.loading = false;
                                }
                            );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.toastrService.danger(
                error?.message,
                'Error',
                AppConstants.TOAST_CONFIG
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
                                    this.toastrService.success(
                                        '',
                                        'factura cancelada',
                                        AppConstants.TOAST_CONFIG
                                    );
                                    this.store.dispatch(
                                        updateInvoice({ invoice })
                                    );
                                    this.loading = false;
                                },
                                (error: NtError) => {
                                    this.toastrService.danger(
                                        error?.message,
                                        'Error',
                                        AppConstants.TOAST_CONFIG
                                    );
                                    this.loading = false;
                                }
                            );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.toastrService.danger(
                error?.message,
                'Error',
                AppConstants.TOAST_CONFIG
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
        fact.saldoPendiente = this.factura.cfdi.total;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.loading = false;
                this.store.dispatch(updateInvoice({ invoice }));
                this.toastrService.success(
                    'acctualización exitosa',
                    'CFDI Revalidado',
                    AppConstants.TOAST_CONFIG
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.toastrService.danger(
                    error?.message,
                    'Error en la revalidacion del CFDI',
                    AppConstants.TOAST_CONFIG
                );
            }
        );
    }

    generateComplement() {
        this.loading = true;
        let errorMessages = [];
        if (this.payment.monto === undefined) {
            errorMessages.push(
                'El monto del complemento es un valor requerido'
            );
        }
        if (this.payment.monto <= 0) {
            errorMessages.push(
                'El monto del complemento no puede ser igual a 0'
            );
        }
        if (this.payment.monto + this.paymentSum > this.factura.cfdi.total) {
            errorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.payment.moneda !== this.factura.cfdi.moneda) {
            errorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.payment.formaPago === undefined) {
            errorMessages.push('La forma de pago es requerida');
        }
        if (
            this.payment.fechaPago === undefined ||
            this.payment.fechaPago === null
        ) {
            errorMessages.push('La fecha de pago es un valor requerido');
        }
        if (errorMessages.length === 0) {
            this.invoiceService
                .generateInvoiceComplement(this.factura.folio, this.payment)
                .subscribe(
                    (complement) => {
                        this.getInvoiceByFolio(this.folio);
                        /*   this.loadConceptos(); */
                    },
                    (error: NtError) => {
                        this.loading = false;
                        this.toastrService.danger(
                            error?.message,
                            'Error en la revalidacion del CFDI',
                            AppConstants.TOAST_CONFIG
                        );
                    }
                );
        } else {
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
                        this.toastrService.danger(
                            error?.message,
                            'Error en la revalidacion del CFDI',
                            AppConstants.TOAST_CONFIG
                        );
                    }
                );
        }
        console.log('giro ' + JSON.stringify(this.companiesCat));
    }

    onCompanySelected(companyId: string) {
        this.companyInfo = this.emisoresCat.find(
            (c) => c.id === Number(companyId)
        );
        this.companyInfo = this.companiesCat.find(
            (c) => c.id === Number(companyId)
        );
        // TODO Mover todo esta logica a un servicio de contrsuccion
        this.factura.rfcEmisor = this.companyInfo.rfc;
        this.factura.razonSocialEmisor =
            this.companyInfo.razonSocial.toUpperCase();
        this.factura.cfdi.emisor.regimenFiscal = this.companyInfo.regimenFiscal;
        this.factura.cfdi.emisor.rfc = this.companyInfo.rfc;
        this.factura.cfdi.emisor.nombre =
            this.companyInfo.razonSocial.toUpperCase();
        this.factura.cfdi.lugarExpedicion = this.companyInfo.cp;
        this.factura.direccionEmisor =
            this.cfdiValidator.generateCompanyAddress(this.companyInfo);
    }
}
