import { Component, OnInit } from '@angular/core';
import { Cuenta } from '../../../models/cuenta';
import { ActivatedRoute, Router } from '@angular/router';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { Empresa } from '../../../models/empresa';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CompaniesData } from '../../../@core/data/companies-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ResourceFile } from '../../../models/resource-file';
import { FilesData } from '../../../@core/data/files-data';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { NotificationsService } from '../../../@core/util-services/notifications.service';


@Component({
  selector: 'ngx-cuenta-bancaria',
  templateUrl: './cuenta-bancaria.component.html',
  styleUrls: ['./cuenta-bancaria.component.scss']
})
export class CuentaBancariaComponent implements OnInit {

  public cuenta: Cuenta;
  public girosCat: Catalogo[] = [];
  public empresas: Empresa[] = [];
  public banksCat: Catalogo[] = [];
  public accountDocs : ResourceFile[] = [];

  public filterParams: any = { banco: '', empresa: '', cuenta: '', clabe:'', empresarazon:''};

  public formInfo = { giro: '*', linea: 'A', fileDataName: '', doctType: '*'};

  private dataFile: ResourceFile= new ResourceFile();


  public module: string = 'bancos';

  public loading = true;
  public clear = false;

  lastkeydown1: number = 0;
  listEmpresasMatch: any[];
  empresasRfc: any[];
  empresasRazonSocial: any[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private downloadService: DonwloadFileService,
    private accountsService: CuentasData,
    private companiesService: CompaniesData,
    private catalogsService: CatalogsData,
    private resourcesService: FilesData,
    private notificationService: NotificationsService,
  ) { }

  ngOnInit() {
    this.cuenta = new Cuenta();
    this.loading = true;
    this.module = this.router.url.split('/')[2];
      this.route.paramMap.subscribe(route => {
        const empresa = route.get('empresa');
        const cuenta = route.get('cuenta');
        if (empresa !== '*') {
            this.companiesService.getCompanyByRFC(empresa)
              .subscribe((company: Empresa) => {
                this.filterParams.empresarazon = company.razonSocial;
              });
              this.catalogsService.getBancos().then(banks => this.banksCat = banks);
            this.getAccountInfo(empresa, cuenta);
        } else {
          this.catalogsService.getAllGiros().then(cat => this.girosCat = cat);
          this.catalogsService.getBancos().then(banks => this.banksCat = banks);
          this.loading = false;
        }
    });
  }

  

  public async getAccountInfo(rfc: string, cuenta: string) {
    try{
      this.loading = true;
      this.dataFile.tipoArchivo = 'CONTRATO';
      if(cuenta!=='*'){
        this.cuenta = await this.accountsService.getCuentaInfo(rfc, cuenta).toPromise();
        this.accountDocs =  await this.resourcesService.getResourcesByTypeAndReference('CUENTAS_BANCARIAS', this.cuenta.id.toString()).toPromise();
      }else{
        this.cuenta.rfc = rfc;
      }
    } catch( error){
      this.notificationService.sendNotification('danger', error?.message,'Error');
    }
    this.loading = false;
  }

  public async registry() {
    try{
      const errors = this.validateAccount(this.cuenta);
      if(errors.length>0){
        errors.forEach(e=>this.notificationService.sendNotification('warning',e,'Dato requerido'));
      }else {
        this.cuenta = await this.accountsService.insertCuenta(this.cuenta).toPromise();
        this.notificationService.sendNotification('info','La cuenta ha sido creada satisfactoriamente.');
      }
    } catch( error){
      this.notificationService.sendNotification('danger', error?.message,'Error');
    }
  }

  public async update() {

    try{
      const errors = this.validateAccount(this.cuenta);
      if(errors.length>0){
        errors.forEach(e=>this.notificationService.sendNotification('warning',e,'Dato requerido'));
      }else {
      this.cuenta = await this.accountsService.updateCuenta(this.cuenta).toPromise();
      this.notificationService.sendNotification('info','Se actualizado la cuenta satisfactoriamente.');
      }
    } catch(error){
        this.notificationService.sendNotification('danger', error?.message,'Error');
    }
  }

  public async delete() {
    try{
      await this.accountsService.deleteCuenta(this.cuenta.id).toPromise();
      this.notificationService.sendNotification('info', 'La cuenta fue borrada exitosamente');
    } catch( error){
      this.notificationService.sendNotification('danger', error?.message,'Error');
    }
  }

  public async onGiroSelection(giroId: string) {
    const value = +giroId;
    this.formInfo.giro = giroId;
    if (isNaN(value)) {
      this.empresas = [];
    } else {
      try{
        this.empresas = await this.companiesService.getCompaniesByLineaAndGiro(this.formInfo.linea, Number(giroId)).toPromise();
      } catch( error){
        this.notificationService.sendNotification('danger', error?.message,'Error');
      }
    }
  }

  public async onLineaSelection() {
  
  this.empresas = [];
  if (this.formInfo.giro === '*') {
    this.empresas = [];
  } else {
    try{
      this.empresas = await this.companiesService.getCompaniesByLineaAndGiro(this.formInfo.linea, Number(this.formInfo.giro)).toPromise();
    } catch( error){
      this.notificationService.sendNotification('danger', error?.message,'Error');
    }
    }
  }

  public onEmpresaSelected(rfc: string) {
    this.cuenta.rfc = rfc;
  }


  public fileDataUploadListener(event: any): void {
    let reader = new FileReader();
    this.dataFile = new ResourceFile();
    if (event.target.files && event.target.files.length > 0) {
      let file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.fileDataName = file.name;
        this.dataFile.extension= file.name.substring(file.name.lastIndexOf('.'),file.name.length);
        this.dataFile.data = reader.result.toString();
      };
      reader.onerror = (error) => { this.notificationService.sendNotification('warning', 'Error cargando el archivo'); };
    }
  }

  private sleep(duration) {
    return new Promise<void>(resolve => {
      setTimeout(() => {
        resolve()
      }, duration * 1000)
    })
  }


  public async fileDocumentUpload():  Promise<void>{
    try{
      this.loading = true;
      this.dataFile.tipoRecurso = 'CUENTAS_BANCARIAS';
      this.dataFile.referencia = this.cuenta.id;
      this.dataFile.tipoArchivo = this.formInfo.doctType;
      await this.resourcesService.insertResourceFile(this.dataFile).toPromise();
      this.formInfo.fileDataName = '';
      this.formInfo.doctType = '*';
      await this.sleep(1);
      this.accountDocs =  await this.resourcesService.getResourcesByTypeAndReference('CUENTAS_BANCARIAS', this.cuenta.id.toString()).toPromise();
      this.notificationService.sendNotification('info', 'El archivo se cargo correctamente');
    } catch(error){
      console.error(error);
      this.formInfo.fileDataName = '';
      this.formInfo.doctType = '*';
      this.notificationService.sendNotification('danger', error?.message,'Error');
    }
    this.loading = false;
  }

  public goToCompany(){
    this.router.navigate([`./pages/${this.module}/empresa/${this.cuenta.rfc}`]);
  }

  public downloadFile(file: ResourceFile){
    const path: string =  `/recursos/${file.tipoRecurso}/referencias/${file.referencia}/files/${file.tipoArchivo}`;
    this.downloadService.dowloadResourceFile(path,`${file.referencia}_${file.tipoArchivo}`);
  }


  private validateAccount(cuenta : Cuenta) : string[] {
    const errors = [];
    if(cuenta.banco === undefined || cuenta.banco === '' || cuenta.banco == null ){
      errors.push('El banco es un valor requerido')
    }
    if(cuenta.cuenta === undefined || cuenta.cuenta === '' || cuenta.cuenta == null ){
      errors.push('El numero de cuenta es un valor requerido')
    }
    if(cuenta.clabe === undefined || cuenta.clabe === '' || cuenta.clabe == null ){
      errors.push('El numero de CLABE es un valor requerido')
    }
    if(cuenta.sucursal === undefined || cuenta.sucursal === '' || cuenta.sucursal == null ){
      errors.push('La sucursal es un valor requerido')
    }
    return errors;
  }
}
