import { Component, OnInit } from '@angular/core';
import { GenericPage } from '../../../models/generic-page';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { Router, ActivatedRoute } from '@angular/router';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { FilesData } from '../../../@core/data/files-data';
import { UtilsService } from '../../../@core/util-services/utils.service';
import { Factura } from '../../../@core/models/factura';
import { DatePipe } from '@angular/common';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { NtError } from '../../../@core/models/nt-error';

@Component({
    selector: 'ngx-invoice-reports',
    templateUrl: './invoice-reports.component.html',
    styleUrls: ['./invoice-reports.component.scss'],
})
export class InvoiceReportsComponent implements OnInit {
    public module: string = 'promotor';
    public page: GenericPage<any> = new GenericPage();
    public pageSize = '10';
    public filterParams: any = {
        emisor: '',
        remitente: '',
        prefolio: '',
        status: '*',
        since: undefined,
        to: undefined,
        tipoDocumento: '*',
        metodoPago: '*',
        saldoPendiente: '0',
        lineaEmisor: '',
        solicitante: '',
        page: '0',
        size: '10',
    };
    public loading = false;

    constructor(
        private invoiceService: InvoicesData,
        private router: Router,
        private downloadService: DonwloadFileService,
        private notificationService: NotificationsService,
        private filesService: FilesData,
        public datepipe: DatePipe,
        private utilsService: UtilsService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        const date: Date = new Date();
        const offsetHrs = date.getTimezoneOffset() / 60;
        this.module = this.router.url.split('/')[2];

        this.route.queryParams.subscribe((params) => {
            if (!this.utilsService.compareParams(params, this.filterParams)) {
                this.filterParams = { ...this.filterParams, ...params };
                this.filterParams.to =
                    params.to === undefined
                        ? new Date(
                              date.getFullYear(),
                              date.getMonth(),
                              date.getDate() + 1
                          )
                        : new Date(
                              `${this.filterParams.to}T00:00:00-0${offsetHrs}:00`
                          );
                this.filterParams.since =
                    params.since === undefined
                        ? new Date(date.getFullYear(), date.getMonth(), 1)
                        : new Date(
                              `${this.filterParams.since}T00:00:00-0${offsetHrs}:00`
                          );

                switch (this.module) {
                    case 'promotor':
                        this.filterParams.solicitante =
                            sessionStorage.getItem('email');
                        this.updateDataTable();
                        break;
                    case 'operaciones':
                    case 'contabilidad':
                    case 'administracion':
                    case 'tesoreria':
                    case 'soporte':
                        this.updateDataTable();
                        break;
                    default:
                        this.filterParams = {
                            ...this.filterParams,
                            ...params,
                        };
                        this.filterParams.solicitante =
                            sessionStorage.getItem('email');
                        this.updateDataTable();
                        break;
                }
            }
        });
    }

    public onPayStatus(payStatus: string) {
        this.filterParams.payStatus = payStatus;
    }

    public onValidationStatus(validationStatus: string) {
        this.filterParams.status = validationStatus;
    }

    public redirectToCfdi(folio: string) {
        switch (this.module) {
            case 'promotor':
                this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
                break;
            case 'tesoreria':
                this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
                break;
            case 'operaciones':
                switch (this.filterParams.lineaEmisor) {
                    case 'A':
                        this.router.navigate([
                            `./pages/operaciones/revision/${folio}`,
                        ]);
                        break;
                    case 'B':
                        this.router.navigate([
                            `./pages/operaciones/cfdi/B/${folio}`,
                        ]);
                        break;
                    case 'C':
                        this.router.navigate([
                            `./pages/operaciones/cfdi/C/${folio}`,
                        ]);
                        break;
                    default:
                        this.router.navigate([
                            `./pages/promotor/precfdi/${folio}`,
                        ]);
                        break;
                }
                break;
            case 'contabilidad':
                this.router.navigate([`./pages/contabilidad/cfdi/${folio}`]);
                break;
            case 'soporte':
                this.router.navigate([`./pages/contabilidad/cfdi/${folio}`]);
                break;
            case 'administracion':
                this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
                break;
            default:
                this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
        }
    }

    public redirectToDevolutionDetails(folio: string) {
        switch (this.module) {
            case 'operaciones':
                this.router.navigate([
                    `./pages/operaciones/facturas/${folio}/devoluciones`,
                ]);
                break;
            case 'tesoreria':
                this.router.navigate([
                    `./pages/tesoreria/facturas/${folio}/devoluciones`,
                ]);
                break;
            case 'administracion':
                this.router.navigate([
                    `./pages/administracion/devoluciones/${folio}/ajustes`,
                ]);
                break;
            default:
                break;
        }
    }

    public redirectToPreferences(folio: string) {
        this.router.navigate([
            `./pages/promotor/precfdi/${folio}/preferencias`,
        ]);
    }

    public updateDataTable(currentPage?: number, pageSize?: number) {
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        params.page =
            currentPage !== undefined ? currentPage : this.filterParams.page;
        params.size =
            pageSize !== undefined ? pageSize : this.filterParams.size;

        if (
            this.module == 'operaciones' ||
            this.module == 'contabilidad' ||
            this.module == 'administracion' ||
            this.module == 'soporte'
        ) {
            this.router.navigate([`./pages/${this.module}/reportes`], {
                queryParams: params,
            });
        } else {
            this.router.navigate([`./pages/promotor/reportes`], {
                queryParams: params,
            });
        }
        this.invoiceService
            .getInvoices(params)
            .subscribe((result: GenericPage<any>) => (this.page = result));
    }

    public onChangePageSize(pageSize: number) {
        this.updateDataTable(this.page.number, pageSize);
    }

    public downloadInvoicesReports() {
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        if (this.module === 'promotor') {
            params.solicitante = sessionStorage.getItem('email');
        }
        params.page = 0;
        params.size = 10000;
        this.invoiceService.getInvoicesReports(params).subscribe(
            (file) => {
                this.downloadService.downloadFile(
                    file.data,
                    `facturas-${this.datepipe.transform(
                        new Date(),
                        'yyyy-MM-dd'
                    )}.xlsx`,
                    'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,'
                );
            },
            (error: NtError) =>
                this.notificationService.sendNotification(
                    'warning',
                    error.message,
                    'Error en la descarga'
                )
        );
    }

    public downloadComplementReports() {
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        params.page = 0;
        params.size = 10000;
        this.invoiceService.getComplementReports(params).subscribe(
            (file) => {
                this.downloadService.downloadFile(
                    file.data,
                    `complementos-${this.datepipe.transform(
                        new Date(),
                        'yyyy-MM-dd'
                    )}.xlsx`,
                    'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,'
                );
            },
            (error: NtError) =>
                this.notificationService.sendNotification(
                    'warning',
                    error.message,
                    'Error en la descarga'
                )
        );
    }

    public downloadPdf(emisor: string, receptor: string, folio: string) {
        this.loading = true;
        this.filesService.getFacturaFile(folio, 'PDF').subscribe(
            (file) => {
                this.downloadService.downloadFile(
                    file.data,
                    `${emisor}_${receptor}_${folio}.pdf`,
                    'application/pdf;'
                );
                this.loading = false;
            },
            (error) => {
                console.error('Error recovering PDF file:', error);
                this.loading = false;
            }
        );
    }

    public downloadAcuseCancelacion(
        emisor: string,
        receptor: string,
        folio: string
    ) {
        this.loading = true;
        this.filesService.getFacturaFile(folio, 'ACUSE_CANCELACION').subscribe(
            (file) => {
                this.downloadService.downloadFile(
                    file.data,
                    `${emisor}_${receptor}_${folio}.xml`,
                    'text/xml;charset=utf8;'
                );
                this.loading = false;
            },
            (error) => {
                console.error('Error recovering XML file', error);
                this.loading = false;
            }
        );
    }

    public downloadXml(emisor: string, receptor: string, folio: string) {
        this.loading = true;
        this.filesService.getFacturaFile(folio, 'XML').subscribe(
            (file) => {
                this.downloadService.downloadFile(
                    file.data,
                    `${emisor}_${receptor}_${folio}.xml`,
                    'text/xml;charset=utf8;'
                );
                this.loading = false;
            },
            (error) => {
                console.error('Error recovering XML file', error);
                this.loading = false;
            }
        );
    }

    public reSendEmail(folio: string) {
        this.loading = true;
        this.invoiceService.reSendEmail(folio).subscribe(
            () => {
                this.notificationService.sendNotification(
                    'success',
                    `Se ha enviado correctamente el correo de la factura con folio ${folio}`,
                    'Envio de correo exitoso'
                );
                this.loading = false;
            },
            (error) => {
                console.error('Error sending email', error);
                this.loading = false;
            }
        );
    }
}
