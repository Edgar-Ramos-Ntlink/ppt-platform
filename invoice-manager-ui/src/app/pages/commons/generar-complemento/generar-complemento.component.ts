import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { PaymentsData } from '../../../@core/data/payments-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { PagoBase } from '../../../models/pago-base';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { HttpErrorResponse } from '@angular/common/http';
import { Factura } from '../../../@core/models/factura';
import { AppState } from '../../../reducers';
import { select, Store } from '@ngrx/store';
import { invoice } from '../../../@core/core.selectors';
import { updateInvoice } from '../../../@core/core.actions';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'ngx-generar-complemento',
    templateUrl: './generar-complemento.component.html',
    styleUrls: ['./generar-complemento.component.scss'],
})
export class GenerarComplementoComponent implements OnInit {
    public factura: Factura;
    @Input() loading: boolean;
    @Output() myEvent = new EventEmitter<string>();

    public complementPayTypeCat: Catalogo[] = [];
    public paymentForm = {
        coin: '*',
        payType: '*',
        bank: '*',
        filename: '',
        successPayment: false,
    };
    public newPayment: PagoBase = new PagoBase();
    public invoicePayments = [];
    public payErrorMessages: string[] = [];
    public validationCat: Catalogo[] = [];
    public paymentSum: number = 0;

    constructor(
        private paymentsService: PaymentsData,
        private invoiceService: InvoicesData,
        private store: Store<AppState>,
        public datepipe: DatePipe
    ) {}

    ngOnInit() {
        this.store
            .pipe(select(invoice))
            .subscribe((fact) => (this.factura = fact));
        this.paymentsService
            .getFormasPago()
            .subscribe((payTypes) => (this.complementPayTypeCat = payTypes));
        this.initVariables();
        this.loading = false;
        this.newPayment.monto = this.factura.saldoPendiente;
    }

    public initVariables() {
        this.newPayment.formaPago = '*';
        this.payErrorMessages = [];
    }

    generateComplement() {
        this.loading = true;
        this.newPayment.fechaPago = this.datepipe.transform(
            this.newPayment.fechaPago,
            'yyyy-MM-dd HH:mm:ss'
        );
        this.payErrorMessages = [];
        if (this.newPayment.monto === undefined) {
            this.payErrorMessages.push(
                'El monto del complemento es un valor requerido'
            );
        }
        if (this.newPayment.monto <= 0) {
            this.payErrorMessages.push(
                'El monto del complemento no puede ser igual a 0'
            );
        }
        if (this.newPayment.monto + this.paymentSum > this.factura.cfdi.total) {
            this.payErrorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.newPayment.moneda !== this.factura.cfdi.moneda) {
            this.payErrorMessages.push(
                'El monto del complemento no puede ser superior al monto total de la factura'
            );
        }
        if (this.newPayment.formaPago === undefined) {
            this.payErrorMessages.push('La forma de pago es requerida');
        }
        if (
            this.newPayment.fechaPago === undefined ||
            this.newPayment.fechaPago === null
        ) {
            this.payErrorMessages.push(
                'La fecha de pago es un valor requerido'
            );
        }
        if (this.payErrorMessages.length === 0) {
            this.invoiceService
                .generateInvoiceComplement(this.factura.folio, this.newPayment)
                .subscribe(
                    (invoice) => {
                        this.store.dispatch(updateInvoice({ invoice }));
                        this.loading = false;
                    },
                    (error: HttpErrorResponse) => {
                        this.payErrorMessages.push(
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
}
