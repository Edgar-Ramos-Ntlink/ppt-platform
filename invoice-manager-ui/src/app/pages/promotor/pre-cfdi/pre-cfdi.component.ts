import { Component, OnInit, OnDestroy } from '@angular/core';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ClientsData } from '../../../@core/data/clients-data';
import { CompaniesData } from '../../../@core/data/companies-data';
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
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { GenericPage } from '../../../models/generic-page';
import { User } from '../../../@core/models/user';
import { CfdiData } from '../../../@core/data/cfdi-data';
import {
    NbComponentStatus,
    NbGlobalPhysicalPosition,
    NbToastrService,
} from '@nebular/theme';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Cfdi } from '../../../@core/models/cfdi/cfdi';
import { Factura } from '../../../@core/models/factura';
import { Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { updateInvoice } from '../../../@core/core.actions';
import { NtError } from '../../../@core/models/nt-error';

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
    public validationCat: Catalogo[] = [];

    public clientsCat: Client[] = [];
    public factura: Factura = new Factura();
    public user: User;

    public formInfo = {
        clientName: '',
        clientRfc: '*',
        companyRfc: '',
        giro: '*',
        empresa: '*',
    };
    public clientInfo: Contribuyente;
    public companyInfo: Empresa;

    public loading: boolean = false;
    public clientSearchMsg = '';

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
        private toastrService: NbToastrService,
        private route: ActivatedRoute,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.userService
            .getUserInfo()
            .then((user) => (this.user = user as User));
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
        this.loading = false;
        this.factura.cfdi.moneda = 'MXN';
        this.factura.cfdi.metodoPago = '*';
        this.factura.cfdi.formaPago = '*';
        this.factura.cfdi.receptor.usoCfdi = '*';
        this.clientSearchMsg = '';
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
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
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

    public async buscarClientInfo(razonSocial: string) {
        try {
            if (razonSocial !== undefined && razonSocial.length >= 5) {
                this.clientsCat = await this.clientsService
                    .getClients({
                        promotor: this.user.email,
                        razonSocial: razonSocial,
                        page: '0',
                        size: '20',
                    })
                    .pipe(
                        map(
                            (clientsPage: GenericPage<Client>) =>
                                clientsPage.content
                        )
                    )
                    .toPromise();
                if (this.clientsCat.length > 0) {
                    this.formInfo.clientRfc = this.clientsCat[0].id.toString();
                    this.onClientSelected(this.formInfo.clientRfc);
                } else {
                    this.clientSearchMsg = `No se encuentran  clientes con nombre ${razonSocial}`;
                }
            } else {
                this.clientsCat = [];
                this.clientInfo = undefined;
                this.clientSearchMsg = '';
            }
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
    }

    public onClientSelected(id: string) {
        const value = +id;
        this.clientSearchMsg = '';
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
                this.clientSearchMsg = `El cliente ${client.informacionFiscal.razonSocial} no se encuentra activo,notifique a operciones para activarlo`;
            }
        }
    }

    limpiarForma() {
        this.initInvoice();
        this.clientInfo = undefined;
        this.companyInfo = undefined;
        this.factura = new Factura();
        this.factura.cfdi = new Cfdi();
        this.factura.cfdi.conceptos = [];
    }

    isValidCfdi(): boolean {
        return this.cfdiValidator.validarCfdi(this.factura.cfdi).length === 0;
    }

    public async solicitarCfdi() {
        this.loading = true;

        try {
            this.factura.solicitante = this.user.email;
            this.factura.lineaEmisor = 'A';
            this.factura.lineaRemitente = 'CLIENTE';
            this.factura.metodoPago = this.factura.cfdi.metodoPago;
            const errorMessages = this.cfdiValidator.validarCfdi({
                ...this.factura.cfdi,
            });

            if (errorMessages.length === 0) {
                const invoice = await this.invoiceService
                    .insertNewInvoice(this.factura)
                    .toPromise();
                await this.getInvoiceByFolio(this.folio);
                this.showToast(
                    'info',
                    'Exito!',
                    'El CFDI se solicitó correctamente'
                );
            } else {
                for (const msg of errorMessages) {
                    this.showToast('warning', 'Falta información', msg, true);
                }
            }
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
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
            this.showToast('info', 'Exito!', 'Factura recuperada exitosamente');
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
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

    private showToast(
        type: NbComponentStatus,
        title: string,
        body: string,
        clickdestroy?: boolean
    ) {
        const config = {
            status: type,
            destroyByClick: clickdestroy || false,
            duration: 8000,
            hasIcon: true,
            position: NbGlobalPhysicalPosition.TOP_RIGHT,
            preventDuplicates: true,
        };
        const titleContent = title ? `${title}` : 'xxxx';

        this.toastrService.show(body, titleContent, config);
    }
}
