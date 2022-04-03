import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Factura } from '../../../models/factura';
import { PaymentsData } from '../../../@core/data/payments-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { PagoBase } from '../../../models/pago-base';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { HttpErrorResponse } from '@angular/common/http';
import { Pago } from '../../../@core/models/cfdi/pago';

@Component({
  selector: 'ngx-generar-complemento',
  templateUrl: './generar-complemento.component.html',
  styleUrls: ['./generar-complemento.component.scss']
})
export class GenerarComplementoComponent implements OnInit {

  @Input() factura: Factura;
  @Input() loading: boolean;
  @Output() myEvent = new EventEmitter<string>();

  public complementPayTypeCat: Catalogo[] = [];
  public payment: Pago;
  public paymentForm = { coin: '*', payType: '*', bank: '*', filename: '', successPayment: false };
  public newPayment: PagoBase;
  public invoicePayments = [];
  public payErrorMessages: string[] = [];
  public validationCat: Catalogo[] = [];
  public paymentSum: number = 0;

  public complementos: Factura[] = [];

  constructor(
    private paymentsService: PaymentsData,
    private invoiceService: InvoicesData,
  ) {

   }

  ngOnInit() {
    
    this.paymentsService.getFormasPago().subscribe(payTypes => this.complementPayTypeCat = payTypes);
    this.initVariables();
    this.loading = false;
  }

  public initVariables() {
    this.payment = new Pago();
    this.payment.formaPago = '*'; 
    this.payErrorMessages = [];
  }

   generateComplement() {
    this.loading = true;
    console.log("cargando");
    this.payErrorMessages = [];
    if (this.payment.monto === undefined) {
      this.payErrorMessages.push('El monto del complemento es un valor requerido');
    }
    if (this.payment.monto <= 0) {
      this.payErrorMessages.push('El monto del complemento no puede ser igual a 0');
    }
    if (this.payment.monto + this.paymentSum > this.factura.cfdi.total) {
      this.payErrorMessages.push('El monto del complemento no puede ser superior al monto total de la factura');
    }
    if (this.payment.moneda !== this.factura.cfdi.moneda) {
      this.payErrorMessages.push('El monto del complemento no puede ser superior al monto total de la factura');
    }
    if (this.payment.formaPago === undefined) {
      this.payErrorMessages.push('La forma de pago es requerida');
    }
    if (this.payment.fechaPago === undefined || this.payment.fechaPago === null) {
      this.payErrorMessages.push('La fecha de pago es un valor requerido');
    }
    if (this.payErrorMessages.length === 0) {
        this.invoiceService.generateInvoiceComplement(this.factura.folio, this.payment)
        .subscribe(complement => {
          this.myEvent.emit(this.factura.cfdi.id.toString()); 
          this.loading = false; 
        }, ( error: HttpErrorResponse) => {
          this.payErrorMessages.push((error.error != null && error.error !== undefined)
            ? error.error.message : `${error.statusText} : ${error.message}`);
         this.loading = false;
        });
      }else {
        this.loading = false;
      }
  }

  calculatePaymentSum(complementos: Factura[]) {
    if (complementos.length === 0) {
      this.paymentSum = 0;
    } else {
      this.paymentSum = complementos.map((c: Factura) => c.total).reduce((total, c) => total + c);
    }
  }

}
