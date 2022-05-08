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
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { FilesData } from '../../../@core/data/files-data';
import { CfdiData } from '../../../@core/data/cfdi-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { NtError } from '../../../@core/models/nt-error';
import { initInvoice, updateInvoice } from '../../../@core/core.actions';
import { invoice } from '../../../@core/core.selectors';

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
    // TODO DELETE THIS FIELDS
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
        private invoiceService: InvoicesData,
        private cfdiService: CfdiData,
        private cfdiValidator: CfdiValidatorService,
        private toastrService: NbToastrService,
        private dialogService: NbDialogService,
        private route: ActivatedRoute,
        private store: Store<AppState>
    ) { }

    ngOnInit() {
        this.route.paramMap.subscribe((route) => {
            const folio = route.get('folio');
            this.loading = true;
            this.getInvoiceByFolio(folio)
            this.folio = folio;
        });

        

        this.catalogsService.getAllGiros().then((cat) => (this.girosCat = cat));
        this.store.pipe(select(invoice)).subscribe((fact) => (this.factura = fact));
    }

    ngOnDestroy() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }


    public getInvoiceByFolio(folio: string) {
        this.pagosCfdi = [];
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
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
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.loading = false;
            }
        );
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
