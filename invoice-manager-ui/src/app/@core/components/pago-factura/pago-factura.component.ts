import { Component, OnInit, Input } from '@angular/core';
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
import { NbToastrService } from '@nebular/theme';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { invoice } from '../../core.selectors';
import { bignumber, format } from 'mathjs';
import { InvoicesData } from '../../data/invoices-data';
import { updateComplementosPago, updateInvoice } from '../../core.actions';
import { CfdiData } from '../../data/cfdi-data';
import { AppConstants } from '../../../models/app-constants';
import { DatePipe } from '@angular/common';

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
        private cfdiService: CfdiData,
        private fileService: FilesData,
        private paymentValidator: PagosValidatorService,
        private toastService: NbToastrService,
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

                if(Math.abs(fact.saldoPendiente-this.newPayment.monto)>0.01){
                    this.newPayment.monto = fact.saldoPendiente;
                    this.paymentsService
                    .getPaymentsByFolio(this.factura.folio)
                    .subscribe(
                        (payments: PagoBase[]) =>
                            (this.invoicePayments = payments)
                    );
                }
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
                        this.paymentForm.bankAccount = cuentas[0].id;
                        this.newPayment.banco = cuentas[0].banco;
                        this.newPayment.cuenta = cuentas[0].cuenta;
                    }
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
                this.toastService.warning(
                    'El archivo demasiado grande, intenta con un archivo mas pequeño.',
                    'Archivo demasiado grande',
                    AppConstants.TOAST_CONFIG
                );
            } else {
                reader.readAsDataURL(file);
                reader.onload = () => {
                    this.paymentForm.filename = file.name;
                    this.newPayment.documento = reader.result.toString();
                };
                reader.onerror = (error) => {
                    this.toastService.danger(
                        'Error leyendo el archivo',
                        AppConstants.TOAST_CONFIG
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
            factura.saldoPendiente = +format(
                bignumber(factura.saldoPendiente).add(bignumber(payment.monto))
            );
            const invoice = await this.invoiceService
                .updateInvoice(factura)
                .toPromise();
            this.store.dispatch(updateInvoice({ invoice }));
            if (
                factura.metodoPago === 'PPD' &&
                factura.tipoDocumento === 'Factura'
            ) {
                const complementos = await this.cfdiService
                    .findInvoicePaymentComplementsByFolio(factura.folio)
                    .toPromise();
                this.store.dispatch(updateComplementosPago({ complementos }));
            }
        } catch (error) {
            this.toastService.danger(
                error?.message,
                'Error en el borrado',
                AppConstants.TOAST_CONFIG
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
                    resourceFile.tipoRecurso = 'PAGO';
                    resourceFile.referencia = `${result.id}`;
                    resourceFile.data = payment.documento;
                    this.fileService.insertResourceFile(resourceFile).subscribe(
                        (response) => console.log(response),
                        (e) =>
                            this.toastService.warning(
                                e?.message,
                                'Error cargando imagen pago',
                                AppConstants.TOAST_CONFIG
                            )
                    );
                }

                this.invoicePayments.push(result);
                const factura: Factura = JSON.parse(
                  JSON.stringify(this.factura));
                if (payment.formaPago !== 'CREDITO') {
                    
                    factura.saldoPendiente = +format(
                        bignumber(factura.saldoPendiente).minus(
                            bignumber(payment.monto)
                        )
                    );
                    const invoice = await this.invoiceService
                        .updateInvoice(factura)
                        .toPromise();
                    this.store.dispatch(updateInvoice({ invoice }));
                }
                if (
                  factura.metodoPago === 'PPD' &&
                  factura.tipoDocumento === 'Factura'
              ) {
                  const complementos = await this.cfdiService
                      .findInvoicePaymentComplementsByFolio(factura.folio)
                      .toPromise();
                  this.store.dispatch(
                      updateComplementosPago({ complementos })
                  );
              }

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
                    this.toastService.warning(
                        e,
                        'Error de validacion',
                        AppConstants.TOAST_CONFIG
                    )
                );
            }
        } catch (error) {
            this.toastService.danger(
                error?.message,
                'Error al crear el pago',
                AppConstants.TOAST_CONFIG
            );
        }
        this.loading = false;
    }
}
