import { Component, OnInit, TemplateRef } from '@angular/core';
import { NbDialogService, NbToastrService } from '@nebular/theme';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ClientsData } from '../../../@core/data/clients-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { HttpErrorResponse } from '@angular/common/http';
import { Contribuyente } from '../../../models/contribuyente';
import { Empresa } from '../../../models/empresa';
import { Client } from '../../../models/client';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { ActivatedRoute, Router } from '@angular/router';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { map } from 'rxjs/operators';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { UsersData } from '../../../@core/data/users-data';
import { FilesData } from '../../../@core/data/files-data';
import { GenericPage } from '../../../models/generic-page';
import { User } from '../../../@core/models/user';
import { CfdiData } from '../../../@core/data/cfdi-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { NtError } from '../../../@core/models/nt-error';
import { updateInvoice } from '../../../@core/core.actions';

@Component({
    selector: 'ngx-revision',
    templateUrl: './revision.component.html',
    styleUrls: ['./revision.component.scss'],
})
export class RevisionComponent implements OnInit {
    public folio: string;
    public pagosCfdi: Pago[] = [];
    public girosCat: Catalogo[] = [];
    public companiesCat: Empresa[] = [];
    public validationCat: Catalogo[] = [];

    public clientsCat: Client[] = [];
    public factura: Factura = new Factura();
    
    public soporte: boolean = false;
    public successMessage: string;
    public errorMessages: string[] = [];
    public formInfo = {
        clientName: '',
        clientRfc: '*',
        companyRfc: '',
        giro: '*',
        empresa: '*',
    };
    public clientInfo: Contribuyente;
    public companyInfo: Empresa;

    public loading: boolean = true;

    constructor(
        private catalogsService: CatalogsData,
        private clientsService: ClientsData,
        private companiesService: CompaniesData,
        private invoiceService: InvoicesData,
        private cfdiService: CfdiData,
        private cfdiValidator: CfdiValidatorService,
        private userService: UsersData,
        private filesService: FilesData,
        private downloadService: DonwloadFileService,
        private dialogService: NbDialogService,
        private toastrService: NbToastrService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.loading = true;
        this.initInvoice();
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
                    if (this.folio !== '*') {
                        this.getInvoiceByFolio(this.folio);
                    }
                });
            });
    }

    ngOnDestroy() {
        /** CLEAN VARIABLES **/
        this.factura = new Factura();
    }

    public initInvoice() {
        /** INIT VARIABLES **/
        this.factura = new Factura();
        /* this.loading = false; */
        this.factura.cfdi.moneda = 'MXN';
        this.factura.cfdi.metodoPago = '*';
        this.factura.cfdi.formaPago = '*';
        this.factura.cfdi.receptor.usoCfdi = '*';
        this.errorMessages = [];
        this.successMessage = undefined;
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

    onGiroSelection(giroId: string) {
        const value = +giroId;
        if (isNaN(value)) {
            this.companiesCat = [];
        } else {
            this.companiesService
                .getCompaniesByLineaAndGiro('A', Number(giroId))
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
        this.factura.direccionEmisor =
            this.cfdiValidator.generateCompanyAddress(this.companyInfo);
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

    public generateCreditNoteInvoice(factura: Factura) {
        this.loading = true;
        this.errorMessages = [];
        this.successMessage = undefined;
        const fact = { ...factura };
        fact.cfdi = null;
        fact.statusFactura = this.validationCat.find(
            (v) => v.nombre === fact.statusFactura
        ).id;

        this.invoiceService.generateCreditNote(factura.folio, fact).subscribe(
            (result) => {
                this.successMessage =
                    'La nota de credito se ha generado exitosamente';
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

    public aceptarFactura() {
        this.successMessage = undefined;
        this.errorMessages = [];
        const fact = { ...this.factura };
        fact.validacionOper = true;
        fact.statusFactura = '1';
        this.loading = true;
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

    public async timbrarFactura(factura: Factura, dialog: TemplateRef<any>) {
        this.successMessage = undefined;
        this.errorMessages = [];
        try {
            const fact = { ...factura };
            fact.cfdi = null;
            fact.statusFactura = this.validationCat.find(
                (v) => v.nombre === fact.statusFactura
            ).id;

            let client: Client = await this.clientsService
                .getClientsByPromotorAndRfc(
                    this.factura.solicitante,
                    this.factura.cfdi.receptor.rfc
                )
                .toPromise();

            if (client.activo) {
                this.dialogService
                    .open(dialog, { context: fact })
                    .onClose.subscribe((invoice) => {
                        this.loading = true;
                        if (invoice !== undefined) {
                            this.invoiceService
                                .timbrarFactura(fact.folio, invoice)
                                .subscribe(
                                    (result) => {
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
            } else {
                this.loading = false;
                this.errorMessages.push(
                    'El cliente que solicita la factura se encuentra inactivo'
                );
            }
        } catch (error) {
            this.errorMessages.push(
                error.error != null && error.error != undefined
                    ? error.error.message
                    : `${error.statusText} : ${error.message}`
            );
        }
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

    isValidCfdi(): boolean {
        return this.cfdiValidator.validarCfdi(this.factura.cfdi).length === 0;
    }
}
