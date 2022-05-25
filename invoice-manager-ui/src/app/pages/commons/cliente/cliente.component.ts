import { Component, OnInit } from '@angular/core';
import { Client } from '../../../models/client';
import { ClientsData } from '../../../@core/data/clients-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ActivatedRoute, Router } from '@angular/router';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { ClientsValidatorService } from '../../../@core/util-services/clients-validator.service';
import { ResourceFile } from '../../../models/resource-file';
import { FilesData } from '../../../@core/data/files-data';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { NtError } from '../../../@core/models/nt-error';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

@Component({
  selector: 'ngx-cliente',
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss']
})
export class ClienteComponent implements OnInit {

  public module: string = 'promotor';
  public clientInfo: Client;
  public formInfo: any = { rfc: '', coloniaId: '*', fileDataName: '' };
  public coloniaId: number = 0;
  public colonias = [];
  public regimenes = [];
  public loading: boolean = false;

  private dataFile: ResourceFile;


  constructor(
    private resourcesService: FilesData,
    private downloadService: DonwloadFileService,
    private clientService: ClientsData,
    private clientValidatorService: ClientsValidatorService,
    private notificationService: NotificationsService,
    private catalogsService: CatalogsData,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.module = this.router.url.split('/')[2];
    this.clientInfo = new Client();
    this.clientInfo.pais = 'MEX';
    this.catalogsService.getAllRegimenFiscal().then(reg => this.regimenes = reg);
    /** recovering folio info**/
    this.route.paramMap.subscribe(route => {
      const rfc = route.get('rfc');
      const promotor = route.get('promotor');
      if (rfc !== '*') {
        this.loadClientInfo(rfc, promotor);
      }
    });
  }

  public async loadClientInfo(rfc: string, promotor: string) {
    this.loading = true;
    try {
      this.formInfo.rfc = rfc;
      
      this.clientInfo = await this.clientService.getClientsByPromotorAndRfc(promotor, rfc).toPromise();
      const data: ZipCodeInfo = await this.catalogsService.getZipCodeInfo(this.clientInfo.cp);
      this.colonias = data.colonias;
      let index = 0;
      this.formInfo.coloniaId = 'other';
      data.colonias.forEach(element => {
        if (data.colonias[index] === this.clientInfo.localidad) {
          this.formInfo.coloniaId = index;
        }
        index++;
      });
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message, 'Error');
    }
    this.loading = false;
    this.dataFile = await this.resourcesService.getResourceFile(this.clientInfo.id.toString(),'CLIENTES','DOCUMENTO').toPromise();
  }

  public async updateClient() {
    this.loading = true;
    try {
      const errors: string[] = this.clientValidatorService.validarCliente(this.clientInfo);
      if (errors.length > 0) {
        for (const err of errors) {
          this.notificationService.sendNotification('warning',err, 'Falta información');
        }
        this.loading = false;
        return;
      }
      this.clientInfo = await this.clientService.updateClient(this.clientInfo).toPromise();
      this.notificationService.sendNotification('info', 'La información del cliente fue actualizada satisfactoriamente');
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message, 'Error');
    }
    this.loading = false;
  }

  public async insertClient() {
    const client: Client = { ...this.clientInfo };
    this.loading = true;
    try {
      const errors: string[] = this.clientValidatorService.validarCliente(client);
      if (errors.length > 0) {
        for (const err of errors) {
          this.notificationService.sendNotification('warning',err, 'Falta información');
        }
        this.loading = false;
        return;
      }
      client.correoPromotor = sessionStorage.getItem('email');
      this.clientInfo = await this.clientService.insertNewClient(client).toPromise();

      if(this.dataFile !== undefined){
        await this.uploadFile();
      } else{
        this.notificationService.sendNotification('warning', 'Falta comprobante situación fiscal', 'Sin comprobante de situación fiscal del cliente, este es un documento requerido en CFDI 4.0');
      }
      this.notificationService.sendNotification('info','Cliente creado correctamente');
      this.loading = false;
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message, 'Error');
      this.loading = false;
    }

  }

  public onLocation(index: string) {
    if (index !== 'other' && index !== '*') {
      this.clientInfo.localidad = this.colonias[index];
    }
  }

  public zipCodeInfo(zc: string) {
    if (zc.length > 4 && zc.length < 6) {
      this.colonias = [];
      this.catalogsService.getZipCodeInfo(zc).then(
        (data: ZipCodeInfo) => {
          this.clientInfo.estado = data.estado;
          this.clientInfo.municipio = data.municipio;
          this.colonias = data.colonias;
          this.clientInfo.localidad = data.colonias[0];
          if (data.colonias.length < 1) {
            this.notificationService.sendNotification('warning',`No se ha encontrado información pata el codigo postal ${zc}`);
          }
        }, (error: NtError) => {
          this.notificationService.sendNotification('warning',error?.message, 'Error');
        });
    }
  }

  public validatePercentages() {
    if (this.clientInfo.correoContacto === undefined || this.clientInfo.correoContacto.length < 1) {
      this.clientInfo.correoContacto = 'Sin asignar';
      this.clientInfo.porcentajeContacto = 0;
      this.clientInfo.porcentajeDespacho = 16 - this.clientInfo.porcentajeCliente - this.clientInfo.porcentajePromotor;
    }
  }

  public async toggleOn() {

    const client: Client = { ... this.clientInfo };
    client.activo = true;
    this.loading = true;
    try {

      this.clientInfo = await this.clientService.updateClient(client).toPromise();
      this.notificationService.sendNotification('info','Cliente activado exitosamente');
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message, 'Error');
    }
    this.loading = false;
  }

  public async toggleOff() {
    const client: Client = { ... this.clientInfo };
    client.activo = false;
    this.loading = true;
    try {

      this.clientInfo = await this.clientService.updateClient(client).toPromise();
      this.notificationService.sendNotification('info',  'Cliente activado exitosamente');
    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.notificationService.sendNotification('danger', 'Error', msg);
    }
    this.loading = false;
  }

  public fileDataUploadListener(event: any): void {
    let reader = new FileReader();
    this.dataFile = new ResourceFile();
    if (event.target.files && event.target.files.length > 0) {
      let file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.fileDataName = file.name;
        this.dataFile.extension = file.name.substring(file.name.lastIndexOf('.'), file.name.length);
        this.dataFile.data = reader.result.toString();
      };
      reader.onerror = (error) => { this.notificationService.sendNotification('danger', 'Error', 'Error cargando el archivo'); };
    }
  }

  public async uploadFile(): Promise<void> {
    try {
      this.loading = true;
      this.dataFile.tipoRecurso = 'CLIENTES';
      this.dataFile.referencia = this.clientInfo.id.toString();
      this.dataFile.tipoArchivo = 'DOCUMENTO';
      await this.resourcesService.insertResourceFile(this.dataFile).toPromise();
      this.notificationService.sendNotification('info','El archivo se cargo correctamente');
      //this.loadCompanyInfo(this.companyInfo.rfc);
      this.formInfo.fileDataName = '';
      this.formInfo.doctType = '*';
      this.loadClientInfo(this.clientInfo.rfc, this.clientInfo.correoPromotor)
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message, 'Error cargando archivo');
    }
    this.loading = false;
  }

  public downloadFile() {
    const path: string = `/recursos/CLIENTES/referencias/${this.clientInfo.id}/files/DOCUMENTO`;
    this.downloadService.dowloadResourceFile(path, `DocumentoRelacionado_${this.clientInfo.rfc}`);
  }

}
