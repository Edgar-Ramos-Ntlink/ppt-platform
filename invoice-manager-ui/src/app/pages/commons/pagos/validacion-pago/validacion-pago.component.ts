import { Component, OnInit, Input, SecurityContext } from '@angular/core';
import { NbComponentStatus, NbDialogRef, NbGlobalPhysicalPosition, NbToastrService } from '@nebular/theme';
import { PagoBase } from '../../../../models/pago-base';
import { FilesData } from '../../../../@core/data/files-data';
import { DomSanitizer, SafeUrl, SafeResourceUrl } from '@angular/platform-browser';
import { DonwloadFileService } from '../../../../@core/util-services/download-file-service';
import { Router } from '@angular/router';
import { ResourceFile } from '../../../../models/resource-file';
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
    private toastrService: NbToastrService,
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
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
    this.loading = false;
  }

  cancel() {
    this.ref.close();
  }
  onRecahzarPago() {
    
    if (this.updatedPayment.comentarioPago == undefined) {
      this.showToast('warning', 'Falta información', 'Es necesaria una razon de rechazo.', true);
      return;
    }
    if (this.updatedPayment.comentarioPago.length < 6) {
      this.showToast('warning', 'Falta información', 'La descripcion de rechazo debe ser mas detallada.', true);
      return;
    }
    this.updatedPayment.statusPago = 'RECHAZADO';
    this.ref.close(this.updatedPayment);

  }

  onValidarPago() {
    this.ref.close(this.updatedPayment);
  }

  //TODO talvez mover a servicio de descarga de imagenes
  onDownload() {
    const data = `data:${this.file.formato}base64,${this.file.data}`;
    this.downloadService.downloadFile(this.file.data,`${this.pago.banco}-${this.pago.acredor}${this.file.extension}`,this.file.formato);
  }

  private showToast(type: NbComponentStatus, title: string, body: string, clickdestroy?: boolean) {
    const config = {
      status: type,
      destroyByClick: clickdestroy || false,
      duration: 10000,
      hasIcon: true,
      position: NbGlobalPhysicalPosition.TOP_RIGHT,
      preventDuplicates: true,
    };
    const titleContent = title ? `${title}` : 'xxxx';

    this.toastrService.show(
      body,
      titleContent,
      config);
  }
}
