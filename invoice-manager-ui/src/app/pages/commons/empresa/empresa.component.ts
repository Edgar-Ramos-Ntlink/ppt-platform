import { Component, OnInit } from '@angular/core';
import { Empresa, Ingresos } from '../../../models/empresa';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CompaniesValidatorService } from '../../../@core/util-services/companies-validator.service';
import { FilesData } from '../../../@core/data/files-data';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { Cuenta } from '../../../models/cuenta';
import { GenericPage } from '../../../models/generic-page';
import { NbComponentStatus, NbDialogService, NbGlobalPhysicalPosition, NbToastrService } from '@nebular/theme';
import { Observacion } from '../../../models/observacion';
import { ObservacionPendientesComponent } from '../observacion-pendientes/observacion-pendientes.component';
import { UsersData } from '../../../@core/data/users-data';
import { ResourceFile } from '../../../models/resource-file';

@Component({
  selector: 'ngx-empresa',
  templateUrl: './empresa.component.html',
  styleUrls: ['./empresa.component.scss']
})
export class EmpresaComponent implements OnInit {

  public companyInfo: Empresa;
  //TODO remove formInfo
  public formInfo: any = { coloniaId: '*', logoFileName:'', keyFileName:'', certFileName:'' };
  public coloniaId: number = 0;
  public colonias = [];
  public paises = ['México'];
  public module: string = 'operaciones';
  
  public girosCat: Catalogo[] = [];
  public errorMessages: string[] = [];
  public cuentas: Cuenta[];
  public totalSaldos: number = 0;

  public documents : ResourceFile[] =[];

  public logo: ResourceFile;
  public key: ResourceFile;
  public cert: ResourceFile;



  constructor(private router: Router,
    private dialogService: NbDialogService,
    private toastrService: NbToastrService,
    private catalogsService: CatalogsData,
    private empresaService: CompaniesData,
    private userService: UsersData,
    private resourcesService: FilesData,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private accountsService: CuentasData,
    private companiesValidatorService: CompaniesValidatorService) { }

  ngOnInit() {
    this.module = this.router.url.split('/')[2];
    this.companyInfo = new Empresa();
    this.companyInfo.regimenFiscal = '*';
    this.companyInfo.giro = '*';
    this.companyInfo.tipo = '*';
    this.companyInfo.pais = 'México';
    this.errorMessages = [];
    this.catalogsService.getAllGiros().then((giros: Catalogo[]) => this.girosCat = giros,
      (error: HttpErrorResponse) => this.errorMessages.push(error.error.message
        || `${error.statusText} : ${error.message}`)).then(() =>

          this.route.paramMap.subscribe(route => {
            const rfc = route.get('rfc');
            if (rfc !== '*') {
              this.loadCompanyInfo(rfc);
            }
          }));
  }


  public async loadCompanyInfo(rfc: string): Promise<void> {

    try {
      this.companyInfo = await this.empresaService.getCompanyByRFC(rfc).toPromise();
      let cpInfo: ZipCodeInfo = await this.catalogsService.getZipCodeInfo(this.companyInfo.cp);

      this.colonias = cpInfo.colonias;
      let index = 0;
      cpInfo.colonias.forEach(element => {
        if (cpInfo.colonias[index] === this.companyInfo.colonia) {
          console.log(`Colonia: ${this.companyInfo.colonia} with index : ${index}`)
          this.formInfo.coloniaId = index;
        }
        index++;
      });

      this.accountsService.getCuentasByCompany(rfc).subscribe( cuentas => this.cuentas = cuentas,(error) => {
        let msg = error.error.message || `${error.statusText} : ${error.message}`;
        this.showToast('danger', 'Error', msg, true);
      });

      this.documents = await this.resourcesService.getResourcesByTypeAndReference('EMPRESAS',rfc).toPromise();
      this.cert  = this.documents.find(d => d.tipoArchivo === 'CERT');
      this.key  = this.documents.find(d => d.tipoArchivo === 'KEY');
      if(this.documents.find(d => d.tipoArchivo === 'LOGO')) { // only logo needs to be loaded from backend
        this.resourcesService.getResourceFile(rfc,'EMPRESAS','LOGO').subscribe((logo)=> this.logo = logo,(error) => {
          let msg = error.error.message || `${error.statusText} : ${error.message}`;
          this.showToast('danger', 'Error', msg, true);
        });
      }

      // removing mandatory files
      this.documents = this.documents.filter(d=>  d.tipoArchivo != 'LOGO' && d.tipoArchivo != 'CERT' && d.tipoArchivo != 'KEY' );


    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }

    /*this.resourcesService.getResourceFile(rfc, 'EMPRESA', 'LOGO')
      .subscribe(logo => this.logo = 'data:image/jpeg;base64,' + logo.data);*/

  }

  openObservaciones() {
    this.dialogService.open(ObservacionPendientesComponent);
  }

  sanitize(url: string) {
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }


  public zipCodeInfo(zipcode: String) {
    let zc = new String(zipcode);
    if (zc.length > 4 && zc.length < 6) {
      this.colonias = [];
      this.catalogsService.getZipCodeInfo(zipcode).then(
        (data: ZipCodeInfo) => {
          this.colonias = data.colonias;
          this.companyInfo.estado = data.estado;
          this.companyInfo.municipio = data.municipio; 
          this.companyInfo.colonia = data.colonias[0];
        },
        (error: HttpErrorResponse) => console.error(error));
    }
  }

  public onLocation(index: string) {
    this.companyInfo.colonia = this.colonias[index];
  }

  public onRegimenFiscalSelected(regimen: string) {
    this.companyInfo.regimenFiscal = regimen;
  }

  public onGiroSelection(giro: string) {
    this.companyInfo.giro = giro;
  }

  public onLineaSelected(linea: string) {
    this.companyInfo.tipo = linea;
  }



  public logoUploadListener(event: any): void {
    const reader = new FileReader();
    this.logo = new ResourceFile();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      if (file.size > 200000) {
        this.showToast('warning','Error', 'La imagen del logo es demasiado grande', true);
      } else {
        reader.readAsDataURL(file);
        reader.onload = () => {
          const filename = file.name as string;

          this.formInfo.logoFileName =  filename;
          this.logo.data = reader.result.toString();
          this.logo.tipoRecurso = 'EMPRESAS';
          this.logo.referencia = this.companyInfo.rfc;
          this.logo.tipoArchivo = 'LOGO';
          this.logo.formato = filename.substring(filename.indexOf('.'),filename.length);
          this.resourcesService.insertResourceFile(this.logo)
            .subscribe(()=> this.showToast('info', 'Exito!', 'El logo se cargo correctamente'),
            (error)=>{
              console.error(error);
              let msg = error.error.message || `${error.statusText} : ${error.message}`;
              this.showToast('danger', 'Error', msg, true);
            });
        };
        reader.onerror = (error) => {
          this.errorMessages.push('Error parsing image file');
          console.error(error);
        };
      }
    }
  }

  public keyUploadListener(event: any): void {
    const reader = new FileReader();
    this.key = new ResourceFile();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.keyFileName = file.name;
        this.key.data = reader.result.toString();
        this.key.tipoRecurso = 'EMPRESAS';
        this.key.referencia = this.companyInfo.rfc;
        this.key.tipoArchivo = 'KEY';
        this.key.formato = '.key';
        this.resourcesService.insertResourceFile(this.key)
        .subscribe(()=> this.showToast('info', 'Exito!', 'El Key se cargo correctamente'),
            (error)=>{
              console.error(error);
              let msg = error.error.message || `${error.statusText} : ${error.message}`;
              this.showToast('danger', 'Error', msg, true);
            });
        //this.companyInfo.llavePrivada = data.substring(data.indexOf('base64') + 7, data.length);
      };
      reader.onerror = (error) => {
        this.showToast('danger','Error', 'Error cargando la llave', true);
        console.error(error);
      };
    }
  }

  public certificateUploadListener(event: any): void {
    let reader = new FileReader();
    this.cert = new ResourceFile();
    if (event.target.files && event.target.files.length > 0) {
      let file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.certificateFileName = file.name;
        this.cert.data = reader.result.toString();
        this.cert.tipoRecurso = 'EMPRESAS';
        this.cert.referencia = this.companyInfo.rfc;
        this.cert.tipoArchivo = 'CERT';
        this.cert.formato = '.cer';
        this.resourcesService.insertResourceFile(this.cert)
        .subscribe(()=> this.showToast('info', 'Exito!', 'El certificado se cargo correctamente'),
            (error)=>{
              console.error(error);
              let msg = error.error.message || `${error.statusText} : ${error.message}`;
              this.showToast('danger', 'Error', msg, true);
            });
        //this.companyInfo.certificado = data.substring(data.indexOf('base64') + 7, data.length);
      };
      reader.onerror = (error) => { this.showToast('danger','Error', 'Error cargando el certificado', true); };
    }
  }

  public async insertNewCompany() {

    try {
      let errorMessages = this.companiesValidatorService.validarEmpresa(this.companyInfo);
      if (errorMessages.length === 0) {
        let user = this.userService.getUserInfo();
        this.companyInfo.creador = (await user).email;
        this.companyInfo = await this.empresaService.insertNewCompany(this.companyInfo).toPromise();
        this.showToast('info', 'Exito!', 'La empresa ha sido creada correctamente');
      } else {
        let fullMessage = 'Falta información por dar de alta: [ ';
        for (const msg of errorMessages) {
          fullMessage += ` ${msg}, `;
        }
        fullMessage+=' ]'
        this.showToast('warning', 'Necesitas completar informacion adicional', fullMessage, true);
      }
    } catch (error) {
      console.error(error);
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
  }

  public async updateCompany() {
    try {
      await this.empresaService.updateCompany(this.companyInfo.rfc, this.companyInfo).toPromise();
      this.showToast('info', 'Exito!', 'La empresa ha sido actualizada correctamente');
    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
  }

  public async inactivateCompany() {
    this.companyInfo.activo = false;
    try {
      await this.empresaService.updateCompany(this.companyInfo.rfc, this.companyInfo).toPromise();
      this.showToast('info', 'Exito!', 'La empresa ha sido desactivada satisfactoriamente');
    } catch (error) {

    }
  }

  public async activateCompany() {
    this.companyInfo.activo = true;
    try {
      await this.empresaService.updateCompany(this.companyInfo.rfc, this.companyInfo).toPromise();
      this.showToast('info', 'Exito!', 'La empresa ha sido activada satisfactoriamente');
    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
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
