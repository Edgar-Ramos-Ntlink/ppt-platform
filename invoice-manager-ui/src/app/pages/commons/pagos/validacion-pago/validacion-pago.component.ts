import { Component, OnInit, Input } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { PagoBase } from '../../../../models/pago-base';
import { NotificationsService } from '../../../../@core/util-services/notifications.service';
@Component({
  selector: 'ngx-validacion-pago',
  templateUrl: './validacion-pago.component.html',
  styleUrls: ['./validacion-pago.component.scss'],
})
export class ValidacionPagoComponent implements OnInit {


  @Input() pago: PagoBase;
  public updatedPayment: PagoBase;
  public loading: boolean = false;

  constructor(protected ref: NbDialogRef<ValidacionPagoComponent>,
    private notificationService: NotificationsService) { }

  ngOnInit() {
    this.loading = false;
    this.updatedPayment = { ... this.pago };
  }

  cancel() {
    this.ref.close();
  }
  onRecahzarPago() {
    
    if (this.updatedPayment.comentarioPago == undefined) {
      this.notificationService.sendNotification('warning','Es necesaria una razon de rechazo.', 'Falta información');
      return;
    }
    if (this.updatedPayment.comentarioPago.length < 6) {
      this.notificationService.sendNotification('warning', 'La descripcion de rechazo debe ser mas detallada.', 'Falta información');
      return;
    }
    this.updatedPayment.statusPago = 'RECHAZADO';
    this.ref.close(this.updatedPayment);

  }

  onValidarPago() {
    this.ref.close(this.updatedPayment);
  }
}
