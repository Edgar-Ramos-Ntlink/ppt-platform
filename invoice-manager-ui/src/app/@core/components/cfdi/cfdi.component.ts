import { Component, OnInit, Input } from '@angular/core';
import { CatalogsData } from '../../data/catalogs-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { DonwloadFileService } from '../../util-services/download-file-service';
import { FilesData } from '../../data/files-data';
import { Router } from '@angular/router';
import { InvoicesData } from '../../data/invoices-data';
import { AppState } from '../../../reducers';
import { select, Store } from '@ngrx/store';
import { cfdi, invoice } from '../../core.selectors';
import { Cfdi } from '../../models/cfdi/cfdi';
import { updateCfdi, updateInvoice } from '../../core.actions';
import { Factura } from '../../models/factura';
import { NtError } from '../../models/nt-error';
import { NotificationsService } from '../../util-services/notifications.service';

@Component({
    selector: 'nt-cfdi',
    templateUrl: './cfdi.component.html',
    styleUrls: ['./cfdi.component.scss'],
})
export class CfdiComponent implements OnInit {
    @Input() public allowEdit: Boolean;

    public factura: Factura;
    public cfdi: Cfdi;
    public isSupport: boolean = false;
    public loading: boolean = false;

    public payTypeCat: Catalogo[] = [];

    constructor(
        private catalogsService: CatalogsData,
        private filesService: FilesData,
        private downloadService: DonwloadFileService,
        private invoiceService: InvoicesData,
        private notificationService: NotificationsService,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        // catalogs info
        this.initVariables();
        this.store.pipe(select(invoice)).subscribe((fact) => {
            this.factura = fact;
        });
        this.store.pipe(select(cfdi)).subscribe((cfdi) => {
            this.cfdi = cfdi;
        });
    }

    initVariables() {
        const user = JSON.parse(sessionStorage.getItem('user'));
        this.isSupport =
            user.roles.find((u) => u.role == 'SOPORTE') != undefined;
        this.catalogsService.getFormasPago().then((cat) => {
            this.payTypeCat = cat;
            if (this.cfdi?.metodoPago && !this.cfdi.folio) {
                this.onPayMethodChange(this.cfdi.metodoPago);
            }
            this.loading = false;
        });
    }

    onPayMethodChange(clave: string) {
        const invoice = JSON.parse(JSON.stringify(this.factura));
        if (clave === 'PPD') {
            invoice.cfdi.formaPago = '99';
            this.catalogsService
                .getFormasPago()
                .then(
                    (cat) => (this.payTypeCat = cat.filter((c) => c.id == '99'))
                );
        } else {
            this.catalogsService
                .getFormasPago()
                .then(
                    (cat) => (this.payTypeCat = cat.filter((c) => c.id != '99'))
                );
            invoice.cfdi.formaPago = '03';
        }
        invoice.cfdi.metodoPago = clave;
        invoice.metodoPago = clave;
        this.store.dispatch(updateInvoice({ invoice }));
    }

    onPayWayChange(clave: string) {
        const cfdi = { ...this.cfdi };
        cfdi.formaPago = clave;
        this.store.dispatch(updateCfdi({ cfdi }));
    }

    onCoinChange(moneda: string) {
        const cfdi = { ...this.cfdi };
        cfdi.moneda = moneda;
        if (moneda === 'MXN') {
            cfdi.tipoCambio = 1.0;
        }
        this.store.dispatch(updateCfdi({ cfdi }));
    }

    updateLugarExpedicion(lugarExpedicion: string) {
        const cfdi = { ...this.cfdi };
        cfdi.lugarExpedicion = lugarExpedicion;
        this.store.dispatch(updateCfdi({ cfdi }));
    }

    updateCfdi() {
        this.loading = true;
        const fact: Factura = JSON.parse(JSON.stringify(this.factura));
        if (fact.tipoDocumento === 'Factura') {
            fact.total = this.cfdi.total;
        }
        fact.metodoPago = this.cfdi.metodoPago;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.loading = false;
                this.store.dispatch(updateInvoice({ invoice }));
                this.notificationService.sendNotification(
                    'success',
                    'actualización exitosa',
                    'CFDI actualizado'
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error en la actualizacion'
                );
            }
        );
    }

    public async downloadPdf() {
        this.filesService
            .getFacturaFile(this.factura.folio, 'PDF')
            .subscribe((file) =>
                this.downloadService.downloadFile(
                    file.data,
                    `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.pdf`,
                    'application/pdf;'
                )
            );
    }
    public async downloadXml() {
        this.filesService
            .getFacturaFile(this.factura.folio, 'XML')
            .subscribe((file) =>
                this.downloadService.downloadFile(
                    file.data,
                    `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.xml`,
                    'text/xml;charset=utf8;'
                )
            );
    }

    public redirectToCfdi(folio: string) {
        this.invoiceService
            .getInvoiceByFolio(folio)
            .toPromise()
            .then((fact) => {
                const url = this.router.url;
                const redirectUrl = `.${url.substring(
                    0,
                    url.lastIndexOf('/')
                )}/${fact.folio}`;
                this.router.navigate([redirectUrl]);
            });
    }

    public goToRelacionado(folio: String) {
        const url = this.router.url;
        const redirectUrl = `.${url.substring(
            0,
            url.lastIndexOf('/')
        )}/${folio}`;
        this.router.navigate([redirectUrl]);
    }
}
