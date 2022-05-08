import { Component, OnInit, OnDestroy } from '@angular/core';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ClientsData } from '../../../@core/data/clients-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { Empresa } from '../../../models/empresa';
import { Client } from '../../../models/client';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { ActivatedRoute, Router } from '@angular/router';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { map } from 'rxjs/operators';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { FilesData } from '../../../@core/data/files-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { CfdiData } from '../../../@core/data/cfdi-data';
import { NbToastrService } from '@nebular/theme';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { addReceptor, initInvoice, updateInvoice, updateReceptorAddress } from '../../../@core/core.actions';
import { NtError } from '../../../@core/models/nt-error';
import { AppConstants } from '../../../models/app-constants';
import { Receptor } from '../../../@core/models/cfdi/receptor';
import { invoice } from '../../../@core/core.selectors';
import { Emisor } from '../../../@core/models/cfdi/emisor';

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

    public factura: Factura = new Factura();

    public formInfo = {
        clientName: '',
        clientRfc: '*',
        companyRfc: '',
        giro: '*',
        empresa: '*',
    };
    public loading: boolean = false;
    public clientSearchMsg = '';

    constructor(
        private catalogsService: CatalogsData,
        private clientsService: ClientsData,
        private companiesService: CompaniesData,
        private invoiceService: InvoicesData,
        private cfdiService: CfdiData,
        private cfdiValidator: CfdiValidatorService,
        private filesService: FilesData,
        private downloadService: DonwloadFileService,
        private toastrService: NbToastrService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) { }

    ngOnInit() {
        this.route.paramMap.subscribe((route) => {
            const folio = route.get('folio');
            
            if (folio === '*') {
                this.loading = true;
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.clientsService
                    .getClientsByPromotor(sessionStorage.getItem('email'))
                    .subscribe((clients) => { this.clientsCat = clients; this.loading = false }, (error) => this.loading = false);
            } else {
                this.getInvoiceByFolio(folio)
            }
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
            this.toastrService.danger(
                error?.message,
                'Error recuperando giros',
                AppConstants.TOAST_CONFIG
            );
        }
    }

    onCompanySelected(companyId: string) {
        const companyInfo = this.companiesCat.find(
            (c) => c.id === Number(companyId)
        );

        const emisor = new Emisor();
        emisor.rfc = companyInfo.rfc.toUpperCase();
        emisor.nombre = companyInfo.razonSocial.toUpperCase();
        emisor.regimenFiscal =
            "601" || companyInfo.regimenFiscal;

        let invoice = JSON.parse(JSON.stringify(this.factura));
        invoice.cfdi.emisor = emisor;
        invoice.rfcEmisor = emisor.rfc;
        invoice.razonSocialEmisor = emisor.nombre;
        invoice.lineaEmisor = companyInfo.tipo;
        invoice.lineaRemitente = 'CLIENTE';
        invoice.cfdi.lugarExpedicion = companyInfo.cp;
        invoice.direccionEmisor = this.cfdiValidator.generateAddress(
            companyInfo
        );

        this.store.dispatch(updateInvoice({ invoice }));
    }


    public onClientSelected(client: Client) {
        if (!client.activo) {
            this.toastrService.warning(
                `El cliente ${client.razonSocial} no se encuentra activo,notifique al supervisor para activarlo`,
                "Cliente inactivo"
            );
            return;
        }

        if (client.regimenFiscal == undefined || client.regimenFiscal == null || client.regimenFiscal === '*') {
            this.toastrService.warning(
                `El cliente ${client.razonSocial} no cuenta con regimen fiscal, delo de alta antes de continuar`,
                "Informacion faltante"
            );
            return;
        }
        let receptor = new Receptor();
        receptor.rfc = client.rfc.toUpperCase();
        receptor.nombre = client.razonSocial.toUpperCase();
        receptor.domicilioFiscalReceptor = client.cp;
        receptor.regimenFiscalReceptor = client.regimenFiscal;

        let address = this.cfdiValidator.generateAddress(client);
        this.store.dispatch(addReceptor({ receptor }));
        this.store.dispatch(updateReceptorAddress({ address }));
    }

    limpiarForma() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    isValidCfdi(): boolean {
        return this.cfdiValidator.validarCfdi(this.factura.cfdi).length === 0;
    }

    public async solicitarCfdi() {
        this.loading = true;
        try {
            let invoice = JSON.parse(JSON.stringify(this.factura));
            invoice.solicitante = sessionStorage.getItem("email");
            invoice.lineaEmisor = "A";
            invoice.lineaRemitente = "CLIENTE";
            invoice.metodoPago = invoice.cfdi.metodoPago;
            invoice.formaPago = invoice.cfdi.formaPago;
            invoice.rfcEmisor = invoice.cfdi.emisor.rfc;
            invoice.razonSocialEmisor = invoice.cfdi.emisor.nombre;
            invoice.rfcRemitente = invoice.cfdi.receptor.rfc;
            invoice.razonSocialRemitente = invoice.cfdi.receptor.nombre;
            if (invoice.metodoPago === 'PPD') {
                invoice.statusFactura = "4";
            } else {
                invoice.statusFactura = "1";
            }

            let errors: string[] = this.cfdiValidator.validarCfdi(invoice.cfdi);
            if (errors.length === 0) {
                console.log("Sending invoice", invoice);
                invoice = await this.invoiceService
                    .insertNewInvoice(invoice)
                    .toPromise();
                this.loading = false;
                this.toastrService.success(
                    "Solicitud de factura enviada correctamente"
                );
                this.store.dispatch(updateInvoice({ invoice }));
            } else {
                errors.forEach((e) =>
                    this.toastrService.warning("Información incompleta", e, AppConstants.TOAST_CONFIG)
                );
                this.loading = false;
            }
        } catch (error) {
            this.loading = false;
            this.toastrService.danger(error?.message, "Error", AppConstants.TOAST_CONFIG);
        }
    }

    public async revalidateInvoice() {
        this.loading = true;
        try {
            const factura = { ...this.factura };

            factura.statusFactura = '1';
            factura.validacionOper = false;
            factura.validacionTeso = false;
            await this.invoiceService.updateInvoice(factura).toPromise();
            await this.getInvoiceByFolio(this.folio);
            this.toastrService.success(
                'operación exitosa',
                'Factura recuperada exitosamente',
                AppConstants.TOAST_CONFIG
            );
        } catch (error) {
            this.toastrService.danger(
                error?.message,
                'Error en la revalidacion del CFDI',
                AppConstants.TOAST_CONFIG
            );
        }
        this.loading = false;
    }

    public returnToSourceFact(idCfdi: number) {
        this.router.navigate([`./pages/operaciones/revision/${idCfdi}`]);
    }

    public goToRelacionado(idCfdi: number) {
        this.router.navigate([`./pages/operaciones/revision/${idCfdi}`]);
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
}
