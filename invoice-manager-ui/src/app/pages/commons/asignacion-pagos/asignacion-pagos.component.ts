import { Component, OnInit } from '@angular/core';
import { PagoBase } from '../../../models/pago-base';
import { GenericPage } from '../../../models/generic-page';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { map } from 'rxjs/operators';
import { ClientsData } from '../../../@core/data/clients-data';
import { Client } from '../../../models/client';
import { User } from '../../../@core/models/user';
import { Contribuyente } from '../../../models/contribuyente';
import { Cuenta } from '../../../models/cuenta';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { FilesData } from '../../../@core/data/files-data';
import { PagosValidatorService } from '../../../@core/util-services/pagos-validator.service';
import { PaymentsData } from '../../../@core/data/payments-data';
import { ResourceFile } from '../../../models/resource-file';
import { HttpErrorResponse } from '@angular/common/http';
import { PagoFactura } from '../../../models/pago-factura';
import { NbDialogRef } from '@nebular/theme';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { Router } from '@angular/router';
import { Factura } from '../../../@core/models/factura';
import { DatePipe } from '@angular/common';
import { Empresa } from '../../../models/empresa';

@Component({
    selector: 'ngx-asignacion-pagos',
    templateUrl: './asignacion-pagos.component.html',
    styleUrls: ['./asignacion-pagos.component.scss'],
})
export class AsignacionPagosComponent implements OnInit {
    public module: string = 'promotor';

    public page: GenericPage<any>;
    public user: User;
    public fileInput: any;
    public paymentForm = { payType: '*', bankAccount: '*', filename: '' };
    public newPayment: PagoBase = new PagoBase();
    public payErrorMessages: string[] = [];
    public successMesagge: string;

    public payTypeCat: Catalogo[] = [];
    public cuentas: Cuenta[];
    public loading: boolean = false;

    public clientsCat: Client[] = [];
    public companiesCat: Empresa[] = [];

    public selectedClient: Client;
    public selectedCompany: Empresa;

    public filterParams = { solicitante: '', emisor: '', remitente: '' };
    constructor(
        protected ref: NbDialogRef<AsignacionPagosComponent>,
        private paymentsService: PaymentsData,
        public datepipe: DatePipe,
        private clientsService: ClientsData,
        private invoiceService: InvoicesData,
        private accountsService: CuentasData,
        private fileService: FilesData,
        private router: Router,
        private paymentValidator: PagosValidatorService
    ) {}

    ngOnInit() {
        this.module = this.router.url.split('/')[2];
        this.successMesagge = '';
        this.newPayment.moneda = 'MXN';
        this.loading = false;
        this.page = new GenericPage();
        this.filterParams = { solicitante: '', emisor: '', remitente: '' };

        this.paymentsService
            .getFormasPago()
            .subscribe((payTypes) => (this.payTypeCat = payTypes));
        if (this.module === 'promotor') {
            this.filterParams.solicitante = sessionStorage.getItem('email');
            this.clientsService
                .getClientsByPromotor(this.filterParams.solicitante)
                .subscribe((clients) => {
                    this.clientsCat = clients;
                });
        } else {
            this.clientsService
                .getClients({ page: '0', size: '50' })
                .pipe(
                    map(
                        (clientsPage: GenericPage<Client>) =>
                            clientsPage.content
                    )
                )
                .subscribe((clients) => {
                    this.clientsCat = clients;
                });
        }
    }

    exit() {
        this.ref.close();
    }

    selectClient(cliente: Client) {
        this.selectedClient = cliente;
        this.filterParams.remitente = cliente.razonSocial;

        this.invoiceService
            .getInvoices({
                remitente: cliente.razonSocial,
                page: 0,
                size: 10000,
            })
            .pipe(
                map((page: GenericPage<Factura>) => {
                    return page.content.map(
                        (f) =>
                            new Empresa(f.rfcEmisor, f.razonSocialEmisor)
                    );
                })
            )
            .subscribe((companies) => {
                // removing duplicted records
                const rfcs = companies.map((c) => c.rfc);
                this.companiesCat = [];
                for (const rfc of rfcs.filter(
                    (item, index) => rfcs.indexOf(item) === index
                )) {
                    this.companiesCat.push(
                        companies.find((c) => c.rfc === rfc)
                    );
                }
            });
    }

    onCompanySelected(company: any) {
        this.selectedCompany = this.companiesCat.find((c) => c.rfc === company);
        this.filterParams.emisor = this.selectedCompany.razonSocial;
        this.updateDataTable(0, 100);
    }

    onPaymentCoinSelected(clave: string) {
        this.newPayment.moneda = clave;
    }

    onPaymentTypeSelected(clave: string) {
        this.newPayment.formaPago = clave;
        if (clave === 'EFECTIVO' || clave === 'CHEQUE' || clave === '*') {
            this.cuentas = [new Cuenta('N/A', 'No aplica', 'Sin especificar')];
            this.paymentForm.bankAccount = 'N/A';
            this.newPayment.banco = 'No aplica';
            this.newPayment.cuenta = 'Sin especificar';
        } else {
            this.accountsService
                .getCuentasByCompany(this.selectedCompany.rfc)
                .subscribe((cuentas) => {
                    this.cuentas = cuentas;
                    this.paymentForm.bankAccount = cuentas[0].id;
                    this.newPayment.banco = cuentas[0].banco;
                    this.newPayment.cuenta = cuentas[0].cuenta;
                });
        }
    }

    onPaymentBankSelected(clave: string) {
        this.newPayment.banco = clave;
    }

    fileUploadListener(event: any): void {
        this.fileInput = event.target;
        const reader = new FileReader();
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.size > 1000000) {
                alert(
                    'El archivo demasiado grande, intenta con un archivo mas pequeño.'
                );
            } else {
                reader.readAsDataURL(file);
                reader.onload = () => {
                    this.paymentForm.filename = file.name;
                    this.newPayment.documento = reader.result.toString();
                };
                reader.onerror = (error) => {
                    this.payErrorMessages.push('Error parsing image file');
                };
            }
        }
    }

    sendPayment() {
        console.log(this.newPayment);
        console.log(this.newPayment.fechaPago);
        const filename = this.paymentForm.filename;
        this.successMesagge = '';
        this.newPayment.fechaPago = this.datepipe.transform(
            this.newPayment.fechaPago,
            'yyyy-MM-dd HH:mm:ss'
        );
        this.payErrorMessages = [];
        const payment = { ...this.newPayment };
        console.log('Validating :', payment);
        for (const f of this.page.content) {
            if (f.pagoMonto !== undefined && f.pagoMonto > 0) {
                payment.facturas.push(
                    new PagoFactura(
                        f.pagoMonto,
                        f.folio,
                        f.razonSocialEmisor,
                        f.razonSocialRemitente
                    )
                );
            }
        }
        payment.solicitante =
            this.module !== 'promotor'
                ? (payment.solicitante = this.page.content[0].solicitante)
                : sessionStorage.getItem('email');
        this.payErrorMessages =
            this.paymentValidator.validatePagoSimple(payment);
        if (this.payErrorMessages.length === 0) {
            this.loading = true;
            payment.acredor = this.selectedCompany.razonSocial;
            payment.deudor = this.selectedClient.razonSocial;
            this.paymentsService.insertNewPayment(payment).subscribe(
                (result) => {
                    const resourceFile = new ResourceFile();
                    resourceFile.tipoArchivo = 'IMAGEN';
                    resourceFile.tipoRecurso = 'PAGOS';
                    resourceFile.extension = filename.substring(
                        filename.indexOf('.'),
                        filename.length
                    );
                    resourceFile.referencia = `${result.id}`;
                    resourceFile.data = payment.documento;
                    this.fileService
                        .insertResourceFile(resourceFile)
                        .subscribe((response) => console.log(response));
                    this.successMesagge = 'Pago creado correctamente';
                    this.updateDataTable();
                    this.loading = false;
                },
                (error: HttpErrorResponse) => {
                    this.loading = false;
                    this.payErrorMessages.push(
                        error.error.message ||
                            `${error.statusText} : ${error.message}`
                    );
                }
            );
        } else {
            this.newPayment.facturas = [];
        }
    }

    public updateDataTable(currentPage?: number, pageSize?: number) {
        const params: any = { ...this.filterParams };
        params.page = currentPage || 0;
        params.size = pageSize || 10;

        this.invoiceService.getInvoices(params).subscribe(
            (result: GenericPage<any>) => (this.page = result),
            (error) => console.log(error)
        );
    }
}
