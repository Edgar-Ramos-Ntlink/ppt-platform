import { Component, OnInit } from '@angular/core';
import { PagoBase } from '../../../models/pago-base';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { Cuenta } from '../../../models/cuenta';
import { PaymentsData } from '../../data/payments-data';
import { CuentasData } from '../../data/cuentas-data';
import { FilesData } from '../../data/files-data';
import { PagosValidatorService } from '../../util-services/pagos-validator.service';
import { ResourceFile } from '../../../models/resource-file';
import { PagoFactura } from '../../../models/pago-factura';
import { Factura } from '../../models/factura';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { invoice } from '../../core.selectors';
import { InvoicesData } from '../../data/invoices-data';
import { initInvoice, updateInvoice } from '../../core.actions';
import { DatePipe } from '@angular/common';
import { NotificationsService } from '../../util-services/notifications.service';
import { NtError } from '../../models/nt-error';

@Component({
    selector: 'nt-pago-factura',
    templateUrl: './pago-factura.component.html',
    styleUrls: ['./pago-factura.component.scss'],
})
export class PagoFacturaComponent implements OnInit {
    public factura: Factura;
    public fileInput: any;

    public paymentForm = { payType: '*', bankAccount: '*', filename: '' };
    public newPayment: PagoBase = new PagoBase();
    public invoicePayments: PagoBase[] = [];
    public paymentSum: number = 0;
    public payTypeCat: Catalogo[] = [];
    public cuentas: Cuenta[];
    public loading: boolean = false;

    constructor(
        private paymentsService: PaymentsData,
        private accountsService: CuentasData,
        private invoiceService: InvoicesData,
        public datepipe: DatePipe,
        private fileService: FilesData,
        private paymentValidator: PagosValidatorService,
        private notificationService: NotificationsService,
        private store: Store<AppState>
    ) {
        this.store.pipe(select(invoice)).subscribe((fact) => {
            this.factura = fact;
            if (fact?.folio) {
                const user = JSON.parse(sessionStorage.getItem('user'));
                this.paymentsService
                    .getFormasPago(user.roles)
                    .subscribe(
                        (paymentForms) => (this.payTypeCat = paymentForms)
                    );
                    this.newPayment.monto = fact.saldoPendiente;
                    this.paymentsService
                        .getPaymentsByFolio(this.factura.folio)
                        .subscribe(
                            (payments: PagoBase[]) =>
                                (this.invoicePayments = payments)
                        );
            }
        });
        this.newPayment.moneda = 'MXN';
        this.newPayment.facturas = [new PagoFactura()];
    }

    ngOnInit() {}

    /******* PAGOS ********/

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
                .getCuentasByCompany(this.factura.rfcEmisor)
                .subscribe((cuentas) => {
                    if (cuentas.length > 0) {
                        this.cuentas = cuentas;
                        this.paymentForm.bankAccount = cuentas[0].cuenta;
                        this.newPayment.banco = cuentas[0].banco;
                        this.newPayment.cuenta = cuentas[0].cuenta;
                    }
                });
        }
    }

    onPaymentBankSelected(cuenta:string) {
        this.newPayment.cuenta = cuenta;
    }

    fileUploadListener(event: any): void {
        this.fileInput = event.target;
        const reader = new FileReader();
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.size > 1000000) {
                this.notificationService.sendNotification(
                    'warning',
                    'El archivo demasiado grande, intenta con un archivo mas pequeño.',
                    'Archivo demasiado grande'
                );
            } else {
                reader.readAsDataURL(file);
                reader.onload = () => {
                    this.paymentForm.filename = file.name;
                    this.newPayment.documento = reader.result.toString();
                };
                reader.onerror = (error) => {
                    this.notificationService.sendNotification(
                        'danger',
                        'Error en la lectura del archivo'
                    );
                };
            }
        }
    }

    public async deletePayment(index: number) {
        this.loading = true;
        try {
            const payment: PagoFactura = JSON.parse(
                JSON.stringify(this.invoicePayments[index])
            );
            await this.paymentsService.deletePayment(payment.id).toPromise();
            this.invoicePayments.splice(index, 1);
            const factura: Factura = JSON.parse(JSON.stringify(this.factura));
            this.updateInvoiceByFolio(factura.folio);
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error en el borrado del pago'
            );
        }
        this.loading = false;
    }

    public async sendPayment() {
        this.loading = true;
        try {
            const payment: PagoBase = JSON.parse(
                JSON.stringify(this.newPayment)
            );
            payment.facturas[0].folio = this.factura.folio;
            payment.facturas[0].monto = payment.monto;
            payment.acredor = this.factura.razonSocialEmisor;
            payment.deudor = this.factura.razonSocialRemitente;
            payment.solicitante = sessionStorage.getItem('email');
            payment.fechaPago = this.datepipe.transform(
                this.newPayment.fechaPago,
                'yyyy-MM-dd HH:mm:ss'
            );
            const errors = this.paymentValidator.validatePago(
                payment,
                this.factura
            );
            if (errors.length === 0) {
                const result = await this.paymentsService
                    .insertNewPayment(payment)
                    .toPromise();
                if (
                    payment.formaPago === 'DEPOSITO' ||
                    payment.formaPago === 'TRANSFERENCIA'
                ) {
                    const resourceFile = new ResourceFile();
                    resourceFile.tipoArchivo = 'IMAGEN';
                    resourceFile.tipoRecurso = 'PAGOS';

                    resourceFile.extension =
                        this.paymentForm.filename.substring(
                            this.paymentForm.filename.indexOf('.'),
                            this.paymentForm.filename.length
                        );
                    resourceFile.referencia = `${result.id}`;
                    resourceFile.data = payment.documento;

                    this.fileService.insertResourceFile(resourceFile).subscribe(
                        (response) => console.log(response),
                        (e) =>
                            this.notificationService.sendNotification(
                                'warning',
                                e?.message,
                                'Error cargando imagen pago'
                            )
                    );
                }

                this.invoicePayments.push(result);
                const factura: Factura = JSON.parse(
                    JSON.stringify(this.factura)
                );
                this.updateInvoiceByFolio(factura.folio);

                this.newPayment = new PagoBase();
                this.newPayment.moneda = 'MXN';
                this.newPayment.facturas = [new PagoFactura()];
                this.paymentForm = {
                    payType: '*',
                    bankAccount: '*',
                    filename: '',
                };
                if (this.fileInput !== undefined) {
                    this.fileInput.value = '';
                }
            } else {
                errors.forEach((e) =>
                    this.notificationService.sendNotification(
                        'warning',
                        e,
                        'Error de validacion'
                    )
                );
            }
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error en la creacion del pago'
            );
        }
        this.loading = false;
    }

    public updateInvoiceByFolio(folio: string) {
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error?.message,
                    'Error'
                );
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.loading = false;
            }
        );
    }

    public openComprobante(id: Number) {
        window.open(`../api/pagos/${id}/comprobante`, '_blank');
    }
}
