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

@Component({
  selector: 'ngx-empresa',
  templateUrl: './empresa.component.html',
  styleUrls: ['./empresa.component.scss']
})
export class EmpresaComponent implements OnInit {

  public companyInfo: Empresa;
  public formInfo: any = { rfc: '', message: '', coloniaId: '*', success: '', certificateFileName: '', keyFileName: '', logoFileName: '' };
  public coloniaId: number = 0;
  public colonias = [];
  public paises = ['México'];
  public module: string = 'operaciones';
  public logo: string = '';
  public girosCat: Catalogo[] = [];
  public errorMessages: string[] = [];
  public cuentas: Cuenta[];
  public totalSaldos: number = 0;
 
  constructor(private router: Router,
    private dialogService: NbDialogService,
    private toastrService: NbToastrService,
    private catalogsService: CatalogsData,
    private empresaService: CompaniesData,
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
    //TEST 


    console.log(this.companyInfo.ingresos)
    //TEST
    this.companyInfo.informacionFiscal.pais = 'México';
    this.errorMessages = [];
    this.catalogsService.getAllGiros().then((giros: Catalogo[]) => this.girosCat = giros,
      (error: HttpErrorResponse) => this.errorMessages.push(error.error.message
        || `${error.statusText} : ${error.message}`)).then(() =>

          this.route.paramMap.subscribe(route => {
            const rfc = route.get('rfc');
            if (rfc !== '*') {
              this.empresaService.getCompanyByRFC(rfc)
                .subscribe((company: Empresa) => {
                  this.companyInfo = company;
                  this.companyInfo.ingresos = [new Ingresos("2015", 262727.446), new Ingresos("2016", 342626.55572), new Ingresos("2017", 24567.32334), new Ingresos("2018", 853527.223424), new Ingresos("2019", 422616.55), new Ingresos("2020", 6969992.22)];
                  for (var i of this.companyInfo.ingresos) {
                    this.totalSaldos = this.totalSaldos + i.cantidad;
                  }
                  this.companyInfo.observaciones = [new Observacion(3,"RF34646JU422","operaciones","Activacion","Reactiva esta empresa en el fin porfa","Alfredo"),new Observacion(2,"KDA2363673J","operaciones","Ajustes","Revisar los datos de esta empresa para mañana","Alberto"),new Observacion(1,"RH3308924RQE","contabilidad","Revicion","Hay que reanalizar el sistema en los dias siguientes por problemas en esta empresa","Juan")]
                  this.formInfo.rfc = rfc;
                  this.companyInfo.fielEmpresa = "VIGENTE";
                  this.companyInfo.actividadSAT = "Otros servicios profesionales, científicos y técnicos 100%";
                  console.log("cuentasR3R  "+rfc);
                  this.accountsService.getCuentasByCompany(rfc)
                    .subscribe(c => {
                      this.cuentas = c;
                      console.log("cuentas  "+this.cuentas);
                      
                    });
                  this.catalogsService.getZipCodeInfo(company.informacionFiscal.cp).then(
                    (data: ZipCodeInfo) => {
                      this.colonias = data.colonias;
                      let index = 0;
                      this.formInfo.coloniaId = '*';
                      data.colonias.forEach(element => {
                        if (data.colonias[index] === company.informacionFiscal.localidad) {
                          this.formInfo.coloniaId = index;
                        }
                        index++;
                      });
                    },
                    (error: HttpErrorResponse) => console.error(error));
                }, (error: HttpErrorResponse) => {
                  let msg = error.error.message || `${error.statusText} : ${error.message}`;
                  this.showToast('danger', 'Error', msg, true);
                });
              this.resourcesService.getResourceFile(rfc, 'EMPRESA', 'LOGO')
                .subscribe(logo => this.logo = 'data:image/jpeg;base64,' + logo.data);
            }
          }));



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
          this.companyInfo.informacionFiscal.estado = data.estado;
          this.companyInfo.informacionFiscal.municipio = data.municipio; this.colonias = data.colonias;
          this.companyInfo.informacionFiscal.localidad = data.colonias[0];
        },
        (error: HttpErrorResponse) => console.error(error));
    }
  }

  public onLocation(index: string) {
    this.companyInfo.informacionFiscal.localidad = this.colonias[index];
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


  /*
  logoUploadListener(event: any): void {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      if (file.size > 200000) {
        this.showToast('warning','Error', 'La imagen del logo es demasiado grande', true);
      } else {
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.formInfo.logoFileName = file.name;
          this.companyInfo.logotipo = reader.result.toString();
          this.logo = this.companyInfo.logotipo;
        };
        reader.onerror = (error) => {
          this.errorMessages.push('Error parsing image file');
          console.error(error);
        };
      }
    }
  }

  keyUploadListener(event: any): void {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.keyFileName = file.name;
        const data: string = reader.result.toString();
        this.companyInfo.llavePrivada = data.substring(data.indexOf('base64') + 7, data.length);
      };
      reader.onerror = (error) => {
        this.showToast('danger','Error', 'Error cargando la llave', true);
        console.error(error);
      };
    }
  }

  certificateUploadListener(event: any): void {
    let reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      let file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.certificateFileName = file.name;
        const data: string = reader.result.toString();
        this.companyInfo.certificado = data.substring(data.indexOf('base64') + 7, data.length);
      };
      reader.onerror = (error) => { this.showToast('danger','Error', 'Error cargando el certificado', true); };
    }
  }*/

  public async insertNewCompany() {
    try {
      let errorMessages = this.companiesValidatorService.validarEmpresa(this.companyInfo);
      if (errorMessages.length === 0) {
        await this.empresaService.insertNewCompany(this.companyInfo).toPromise();
        this.showToast('info', 'Exito!', 'La empresa ha sido creada correctamente');
      } else {
        let fullMessage = '';
        for (const msg of errorMessages) {
          fullMessage = `<p>${msg}</p>`;
        }
        this.showToast('warning', 'Algunos errores', fullMessage, true);
      }
    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
  }

  public async updateCompany() {
    try {
      await this.empresaService.updateCompany(this.companyInfo.informacionFiscal.rfc, this.companyInfo).toPromise();
      this.showToast('info', 'Exito!', 'La empresa ha sido actualizada correctamente');
    } catch (error) {
      let msg = error.error.message || `${error.statusText} : ${error.message}`;
      this.showToast('danger', 'Error', msg, true);
    }
  }

  public async inactivateCompany() {
    this.companyInfo.activo = false;
    try {
      await this.empresaService.updateCompany(this.companyInfo.informacionFiscal.rfc, this.companyInfo).toPromise();
      this.showToast('info', 'Exito!', 'La empresa ha sido desactivada satisfactoriamente');
    } catch (error) {

    }
  }

  public async activateCompany() {
    this.companyInfo.activo = true;
    try {
      await this.empresaService.updateCompany(this.companyInfo.informacionFiscal.rfc, this.companyInfo).toPromise();
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
      duration: 2500,
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
