import { Component, OnInit, Input } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { PagoBase } from '../../../../models/pago-base';
import { FilesData } from '../../../../@core/data/files-data';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { DonwloadFileService } from '../../../../@core/util-services/download-file-service';
import { Router } from '@angular/router';
import { ResourceFile } from '../../../../models/resource-file';
import { NotificationsService } from '../../../../@core/util-services/notifications.service';
@Component({
  selector: 'ngx-validacion-pago',
  templateUrl: './validacion-pago.component.html',
  styleUrls: ['./validacion-pago.component.scss'],
})
export class ValidacionPagoComponent implements OnInit {


  @Input() pago: PagoBase;
  public comprobanteUrl: SafeUrl;
  public updatedPayment: PagoBase;
  public loading: boolean = false;
  public module : string = 'operaciones';

  public file: ResourceFile;

  constructor(protected ref: NbDialogRef<ValidacionPagoComponent>,
    private notificationService: NotificationsService,
    private filesService: FilesData,
    private router: Router,
    private downloadService: DonwloadFileService,
    private sanitizer: DomSanitizer) { }

  ngOnInit() {
    this.loading = false;
    this.module = this.router.url.split('/')[2];
    this.file = undefined;
    this.mostrarComprobante(this.pago);
    this.updatedPayment = { ... this.pago };
  }



  public async mostrarComprobante(pago: PagoBase) {
    
    this.comprobanteUrl = undefined;
    this.loading = true;
    try{
      if (pago.formaPago !== 'CREDITO' && pago.formaPago !== 'EFECTIVO') {
        this.file = await this.filesService.getResourceFile(`${pago.id}`, 'PAGOS', 'IMAGEN').toPromise();
        const url = `data:${this.file.formato}base64,${this.file.data}`;
        this.comprobanteUrl = this.sanitizer.bypassSecurityTrustUrl(url);
      }
    }catch(error){
      this.notificationService.sendNotification('danger',error?.message, 'Error');
    }
    this.loading = false;
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

  onDownload() {
    this.downloadService.downloadFile(this.file.data,`${this.pago.banco}-${this.pago.acredor}${this.file.extension}`,this.file.formato);
  }

}
