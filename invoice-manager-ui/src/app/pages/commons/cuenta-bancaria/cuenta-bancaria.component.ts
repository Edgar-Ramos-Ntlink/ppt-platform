import { Component, OnInit } from '@angular/core';
import { Cuenta } from '../../../models/cuenta';
import { ActivatedRoute } from '@angular/router';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { Empresa } from '../../../models/empresa';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CompaniesData } from '../../../@core/data/companies-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { NbComponentStatus, NbGlobalPhysicalPosition, NbToastrService } from '@nebular/theme';
import { ResourceFile } from '../../../models/resource-file';
import { FilesData } from '../../../@core/data/files-data';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';


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
  public Params: any = { success: '', message: ''};

  public formInfo = { giro: '*', linea: 'A', fileDataName: '', doctType: '*'};

  private dataFile: ResourceFile= new ResourceFile();


  public module: string = 'tesoreria';

  public errorMessages: string[] = [];
  public loading = true;
  public clear = false;

  lastkeydown1: number = 0;
  listEmpresasMatch: any[];
  empresasRfc: any[];
  empresasRazonSocial: any[];

  constructor(
    private route: ActivatedRoute,
    private downloadService: DonwloadFileService,
    private accountsService: CuentasData,
    private companiesService: CompaniesData,
    private catalogsService: CatalogsData,
    private resourcesService: FilesData,
    private toastrService: NbToastrService,
  ) { }

  ngOnInit() {
    this.cuenta = new Cuenta();
    this.loading = true;
    this.errorMessages = [];
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

  public async update() {

    try{
      this.cuenta = await this.accountsService.updateCuenta(this.cuenta).toPromise();
      this.showToast('info', 'Exito!', 'Se actualizado la cuenta satisfactoriamente.');
    } catch( error){
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
    }
  }

  public async getAccountInfo(rfc: string, cuenta: string) {
    try{
      this.loading = true;
      this.dataFile.tipoArchivo = 'CONTRATO';
      if(cuenta!=='*'){
        this.cuenta = await this.accountsService.getCuentaInfo(rfc, cuenta).toPromise();
        this.accountDocs =  await this.resourcesService.getResourcesByTypeAndReference('CUENTAS_BANCARIAS', this.cuenta.id.toString()).toPromise();
      }else{
        this.cuenta.empresa = rfc;
      }
    } catch( error){
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
    }
    this.loading = false;
  }

  public async registry() {
    try{
      this.cuenta = await this.accountsService.insertCuenta(this.cuenta).toPromise();
      this.showToast('info', 'Exito!', 'La cuenta ha sido creada satisfactoriamente.');
    } catch( error){
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
    }
  }

  public async delete() {
    try{
      await this.accountsService.deleteCuenta(this.cuenta.id).toPromise();
      this.showToast('info', 'Exito!', 'La cuenta fue borrada exitosamente');
    } catch( error){
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
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
        let msg = error.error.message || `${error.statusText} : ${error.message}`;
          this.showToast('danger', 'Error', msg, true);
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
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
    }
    }
  }

  public onEmpresaSelected(rfc: string) {
    this.cuenta.empresa = rfc;
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
      reader.onerror = (error) => { this.showToast('danger', 'Error', 'Error cargando el archivo', true); };
    }
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
      this.getAccountInfo(this.cuenta.empresa, this.cuenta.cuenta)
      this.showToast('info', 'Exito!', 'El archivo se cargo correctamente');
    } catch(error){
      console.error(error);
      this.formInfo.fileDataName = '';
      this.formInfo.doctType = '*';
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
    this.loading = false;
  }

  public downloadFile(file: ResourceFile){
    const path: string =  `/recursos/${file.tipoRecurso}/referencias/${file.referencia}/files/${file.tipoArchivo}`;
    this.downloadService.dowloadResourceFile(path,`${file.referencia}_${file.tipoArchivo}`);
  }

  private showToast(type: NbComponentStatus, title: string, body: string, clickdestroy?: boolean) {
    const config = {
      status: type,
      destroyByClick: clickdestroy || false,
      duration: 8000,
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
