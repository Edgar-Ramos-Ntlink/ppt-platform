import { Component, OnInit, TemplateRef } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { UsersData } from '../../../@core/data/users-data';
import { Empresa } from '../../../models/empresa';
import { UsoCfdi } from '../../../models/catalogos/uso-cfdi';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { FilesData } from '../../../@core/data/files-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { ActivatedRoute, Router } from '@angular/router';
import { Client } from '../../../models/client';
import { ClientsData } from '../../../@core/data/clients-data';
import { Contribuyente } from '../../../models/contribuyente';
import { map } from 'rxjs/operators';
import { GenericPage } from '../../../models/generic-page';
import { ClaveProductoServicio } from '../../../models/catalogos/producto-servicio';
import { ClaveUnidad } from '../../../models/catalogos/clave-unidad';
import { PagoBase } from '../../../models/pago-base';
import { NbDialogService, NbToastrService } from '@nebular/theme';
import { PaymentsData } from '../../../@core/data/payments-data';
import { User } from '../../../@core/models/user';
import { CfdiData } from '../../../@core/data/cfdi-data';
import { Concepto } from '../../../@core/models/cfdi/concepto';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Cfdi } from '../../../@core/models/cfdi/cfdi';
import { Factura } from '../../../@core/models/factura';
import { Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { updateInvoice } from '../../../@core/core.actions';
import { NtError } from '../../../@core/models/nt-error';

@Component({
    selector: 'ngx-linea-x',
    templateUrl: './linea-x.component.html',
    styleUrls: ['./linea-x.component.scss'],
})
export class LineaXComponent implements OnInit {
    public girosCat: Catalogo[] = [];
    public emisoresCat: Empresa[] = [];
    public receptoresCat: Empresa[] = [];
    public clientsCat: Client[] = [];
    public prodServCat: ClaveProductoServicio[] = [];
    public claveUnidadCat: ClaveUnidad[] = [];
    public usoCfdiCat: UsoCfdi[] = [];
    public validationCat: Catalogo[] = [];
    public payCat: Catalogo[] = [];
    public devolutionCat: Catalogo[] = [];
    public payTypeCat: Catalogo[] = [
        new Catalogo('01', 'Efectivo'),
        new Catalogo('02', 'Cheque nominativo'),
        new Catalogo('03', 'Transferencia electrónica de fondos'),
        new Catalogo('99', 'Por definir'),
    ];

    public complementPayTypeCat: Catalogo[] = [];
    public newConcep: Concepto;
    public payment: Pago;
    public factura: Factura;
    public folio: string;
    public user: User;

    public complementos: Factura[] = [];

    public pagosCfdi: Pago[] = [];

    public successMessage: string;
    public errorMessages: string[] = [];
    public conceptoMessages: string[] = [];
    public payErrorMessages: string[] = [];

    public LINEAEMISOR: string = 'B';

    public formInfo = {
        emisorRfc: '*',
        receptorRfc: '*',
        giroReceptor: '*',
        giroEmisor: '*',
        lineaReceptor: 'CLIENTE',
        usoCfdi: '*',
        payType: '*',
        clientRfc: '*',
        clientName: '',
        companyRfc: '',
        giro: '*',
        empresa: '*',
    };

    public clientInfo: Contribuyente;
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

    public companiesCat: Empresa[] = [];

    constructor(
        private dialogService: NbDialogService,
        private catalogsService: CatalogsData,
        private clientsService: ClientsData,
        private companiesService: CompaniesData,
        private invoiceService: InvoicesData,
        private cfdiService: CfdiData,
        private filesService: FilesData,
        private userService: UsersData,
        private cfdiValidator: CfdiValidatorService,
        private paymentsService: PaymentsData,
        private downloadService: DonwloadFileService,
        private toastrService: NbToastrService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.loading = true;
        this.userService
            .getUserInfo()
            .then((user) => (this.user = user as User));
        this.initInvoice();
        this.paymentsService
            .getFormasPago()
            .subscribe((payTypes) => (this.complementPayTypeCat = payTypes));
        /* preloaded cats*/
        this.catalogsService
            .getStatusValidacion()
            .then((cat) => (this.validationCat = cat));
        this.catalogsService
            .getAllGiros()
            .then((cat) => (this.girosCat = cat))
            .then(() => {
                this.route.paramMap.subscribe((route) => {
                    this.folio = route.get('folio');
                    this.LINEAEMISOR = route.get('linea');

                    if (this.folio !== '*') {
                        this.getInvoiceByFolio(this.folio);
                    } else {
                        this.initInvoice();
                    }
                });
            });
    }

    ngOnDestroy() {
        /** CLEAN VARIABLES **/
        this.newConcep = new Concepto();
        this.factura = new Factura();
        this.conceptoMessages = [];
        this.payErrorMessages = [];
        this.errorMessages = [];
    }

    public initInvoice() {
        /** INIT VARIABLES **/
        this.newConcep = new Concepto();
        this.factura = new Factura();
        this.errorMessages = [];
        this.loading = false;
        this.factura.cfdi.moneda = 'MXN';
        this.factura.cfdi.metodoPago = '*';
        this.payment = new Pago();
        this.payment.formaPago = '*';
        this.factura.cfdi.formaPago = '*';
        this.factura.cfdi.receptor.usoCfdi = '*';
        this.conceptoMessages = [];
        this.payErrorMessages = [];
        this.loading = false;
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
            },
            (error: NtError) => {
                this.toastrService.danger(error.message);
                this.initInvoice();
            }
        );
    }

    limpiarForma() {
        this.initInvoice();
        this.clientInfo = undefined;
        this.companyInfo = undefined;
        this.successMessage = undefined;
        this.newConcep = new Concepto();
        this.factura = new Factura();
        this.factura.cfdi = new Cfdi();
        this.factura.cfdi.conceptos = [];
        this.errorMessages = [];
    }

    onGiroSelection(giroId: string) {
        const value = +giroId;
        if (isNaN(value)) {
            this.companiesCat = [];
        } else {
            this.companiesService
                .getCompaniesByLineaAndGiro(this.LINEAEMISOR, Number(giroId))
                .subscribe(
                    (companies) => (this.companiesCat = companies),
                    (error: HttpErrorResponse) =>
                        this.errorMessages.push(
                            error.error.message ||
                                `${error.statusText} : ${error.message}`
                        )
                );
        }
    }

    onCompanySelected(companyId: string) {
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
        this.factura.direccionReceptor =
            this.cfdiValidator.generateCompanyAddress(this.companyInfo);
    }

    buscarClientInfo(razonSocial: string) {
        if (razonSocial !== undefined && razonSocial.length > 5) {
            this.clientsService
                .getClients({ razonSocial: razonSocial, page: '0', size: '20' })
                .pipe(
                    map(
                        (clientsPage: GenericPage<Client>) =>
                            clientsPage.content
                    )
                )
                .subscribe(
                    (clients) => {
                        this.clientsCat = clients;
                        if (clients.length > 0) {
                            this.formInfo.clientRfc = clients[0].id.toString();
                            this.onClientSelected(this.formInfo.clientRfc);
                        }
                    },
                    (error: HttpErrorResponse) => {
                        this.errorMessages.push(
                            error.error.message ||
                                `${error.statusText} : ${error.message}`
                        );
                        this.clientsCat = [];
                        this.clientInfo = undefined;
                    }
                );
        } else {
            this.clientsCat = [];
            this.clientInfo = undefined;
        }
    }

    onClientSelected(id: string) {
        const value = +id;
        if (!isNaN(value)) {
            const client = this.clientsCat.find((c) => c.id === Number(value));
            this.clientInfo = client.informacionFiscal;
            // mover esta logica a un servicio de construccion
            this.factura.rfcRemitente = this.clientInfo.rfc;
            this.factura.razonSocialRemitente =
                this.clientInfo.razonSocial.toUpperCase();
            this.factura.cfdi.receptor.rfc = this.clientInfo.rfc;
            this.factura.cfdi.receptor.nombre =
                this.clientInfo.razonSocial.toUpperCase();
            this.factura.direccionReceptor = this.cfdiValidator.generateAddress(
                this.clientInfo
            );
            if (!client.activo) {
                this.errorMessages.push(
                    `El cliente ${client.informacionFiscal.razonSocial} no se encuentra activo en el sistema.`
                );
                this.errorMessages.push(
                    'Notifique al departamento de operaciones,puede proceder a solicitar el pre-CFDI'
                );
            }
        }
    }

    public downloadPdf(folio: string) {
        this.filesService
            .getFacturaFile(folio, 'PDF')
            .subscribe((file) =>
                this.downloadService.downloadFile(
                    file.data,
                    `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.pdf`,
                    'application/pdf;'
                )
            );
    }
    public downloadXml(folio: string) {
        this.filesService
            .getFacturaFile(folio, 'XML')
            .subscribe((file) =>
                this.downloadService.downloadFile(
                    file.data,
                    `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.xml`,
                    'text/xml;charset=utf8;'
                )
            );
    }

    public solicitarCfdi() {
        this.loading = true;
        this.errorMessages = [];
        this.factura.solicitante = this.user.email;
        this.factura.lineaEmisor = this.LINEAEMISOR;
        this.factura.lineaRemitente = this.formInfo.lineaReceptor || 'CLIENTE';
        this.factura.metodoPago = this.factura.cfdi.metodoPago;
        this.factura.statusFactura = '4'; // sets automatically to stamp directly
        this.errorMessages = this.cfdiValidator.validarCfdi({
            ...this.factura.cfdi,
        });
        if (this.errorMessages.length === 0) {
            this.invoiceService.insertNewInvoice(this.factura).subscribe(
                (invoice: Factura) => {
                    this.factura = invoice;
                    this.getInvoiceByFolio(invoice.folio);
                    this.successMessage =
                        'Solicitud de factura enviada correctamente';
                },
                (error: HttpErrorResponse) => {
                    this.loading = false;
                    this.errorMessages.push(
                        error.error != null && error.error !== undefined
                            ? error.error.message
                            : `${error.statusText} : ${error.message}`
                    );
                }
            );
        } else {
            this.loading = false;
        }
    }

    public returnToSourceFact(idCfdi: number) {
        this.successMessage = undefined;
        this.router.navigate([`./pages/operaciones/revision/${idCfdi}`]);
    }

    public goToRelacionado(idCfdi: number) {
        this.successMessage = undefined;
        this.router.navigate([`./pages/operaciones/revision/${idCfdi}`]);
    }

    public linkInvoice(factura: Factura) {
        this.loading = true;
        this.errorMessages = [];
        this.successMessage = undefined;
        const fact = { ...factura };
        fact.cfdi = null;
        fact.statusFactura = this.validationCat.find(
            (v) => v.nombre === fact.statusFactura
        ).id;

        this.invoiceService.generateReplacement(factura.folio, fact).subscribe(
            (result) => {
                this.successMessage =
                    'El documento relacionado se ha generado exitosamente';
                this.getInvoiceByFolio(this.folio);
                this.loading = false;
            },
            (error: HttpErrorResponse) => {
                this.errorMessages.push(
                    error.error.message ||
                        `${error.statusText} : ${error.message}`
                );
                this.loading = false;
            }
        );
    }

    public rechazarFactura() {
        this.loading = true;
        this.successMessage = undefined;
        this.errorMessages = [];
        const fact = { ...this.factura };
        fact.statusFactura = '6'; // update to rechazo operaciones
        this.invoiceService.updateInvoice(fact).subscribe(
            (result) => {
                this.loading = false;
                this.getInvoiceByFolio(this.folio);
            },
            (error: HttpErrorResponse) => {
                this.loading = false;
                this.errorMessages.push(
                    error.error != null && error.error !== undefined
                        ? error.error.message
                        : `${error.statusText} : ${error.message}`
                );
            }
        );
    }

    public timbrarFactura(factura: Factura, dialog: TemplateRef<any>) {
        this.successMessage = undefined;
        this.errorMessages = [];
        const fact = { ...factura };
        fact.cfdi = null;
        fact.statusFactura = this.validationCat.find(
            (v) => v.nombre === fact.statusFactura
        ).id;
        this.dialogService
            .open(dialog, { context: fact })
            .onClose.subscribe((invoice) => {
                this.loading = true;
                if (invoice !== undefined) {
                    this.invoiceService
                        .timbrarFactura(fact.folio, invoice)
                        .subscribe(
                            (result) => {
                                console.log(result);
                                this.loading = false;
                                this.getInvoiceByFolio(this.folio);
                            },
                            (error: HttpErrorResponse) => {
                                this.loading = false;
                                this.errorMessages.push(
                                    error.error != null &&
                                        error.error != undefined
                                        ? error.error.message
                                        : `${error.statusText} : ${error.message}`
                                );
                            }
                        );
                } else {
                    this.loading = false;
                }
            });
    }

    public async cancelarFactura(factura: Factura, dialog: TemplateRef<any>) {
        this.successMessage = undefined;
        this.errorMessages = [];
        console.log('Cancelando factura:', factura.preFolio);
        try {
            const fact = { ...factura };
            fact.motivo = '02';
            fact.cfdi = null;
            fact.statusFactura = this.validationCat.find(
                (v) => v.nombre === fact.statusFactura
            ).id;

            this.dialogService
                .open(dialog, { context: fact })
                .onClose.subscribe((invoice) => {
                    this.loading = true;
                    if (invoice !== undefined) {
                        this.invoiceService
                            .cancelarFactura(fact.folio, fact)
                            .subscribe(
                                (success) => {
                                    this.successMessage =
                                        'Factura correctamente cancelada';
                                    this.getInvoiceByFolio(this.folio);
                                },
                                (error: HttpErrorResponse) => {
                                    this.errorMessages.push(
                                        error.error != null &&
                                            error.error != undefined
                                            ? error.error.message
                                            : `${error.statusText} : ${error.message}`
                                    );
                                    this.loading = false;
                                    console.error(this.errorMessages);
                                }
                            );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.errorMessages.push(
                error.error != null && error.error != undefined
                    ? error.error.message
                    : `${error.statusText} : ${error.message}`
            );
        }
    }

    generateComplement() {
        this.loading = true;
        this.errorMessages = [];
        if (this.payment.monto === undefined) {
            this.errorMessages.push(
                'El monto del complemento es un valor requerido'
            );
        }
        if (this.payment.monto <= 0) {
            this.errorMessages.push(
                'El monto del complemento no puede ser igual a 0'
            );
        }
        if (this.payment.monto + this.paymentSum > this.factura.cfdi.total) {
            this.errorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.payment.moneda !== this.factura.cfdi.moneda) {
            this.errorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.payment.formaPago === undefined) {
            this.errorMessages.push('La forma de pago es requerida');
        }
        if (
            this.payment.fechaPago === undefined ||
            this.payment.fechaPago === null
        ) {
            this.errorMessages.push('La fecha de pago es un valor requerido');
        }
        if (this.errorMessages.length === 0) {
            this.invoiceService
                .generateInvoiceComplement(this.factura.folio, this.payment)
                .subscribe(
                    (complement) => {
                        this.getInvoiceByFolio(this.folio);
                    },
                    (error: HttpErrorResponse) => {
                        this.errorMessages.push(
                            error.error != null && error.error !== undefined
                                ? error.error.message
                                : `${error.statusText} : ${error.message}`
                        );
                        this.loading = false;
                    }
                );
        } else {
            this.loading = false;
        }
    }

    calculatePaymentSum(complementos: Factura[]) {
        if (complementos.length === 0) {
            this.paymentSum = 0;
        } else {
            this.paymentSum = complementos
                .map((c: Factura) => c.total)
                .reduce((total, c) => total + c);
        }
    }
}
