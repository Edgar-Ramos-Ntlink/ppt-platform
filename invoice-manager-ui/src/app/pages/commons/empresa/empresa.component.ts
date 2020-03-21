import { Component, OnInit } from '@angular/core';
import { Empresa } from '../../../models/empresa';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CompaniesValidatorService } from '../../../@core/util-services/companies-validator.service';
import { debug } from 'util';

@Component({
  selector: 'ngx-empresa',
  templateUrl: './empresa.component.html',
  styleUrls: ['./empresa.component.scss']
})
export class EmpresaComponent implements OnInit {

  public companyInfo: Empresa;
  public formInfo : any = {rfc:'',message:'',coloniaId:'*', success:'',certificateFileName:'',keyFileName:'', logoFileName:''};
  public coloniaId: number=0;
  public colonias = [];
  public paises = ['México'];

  public girosCat: Catalogo[] = [];
  public errorMessages: string[] = [];

  constructor(private router: Router,
              private catalogsService: CatalogsData,
              private empresaService: CompaniesData,
              private route: ActivatedRoute,
              private sanitizer: DomSanitizer,
              private companiesValidatorService: CompaniesValidatorService) { }

  ngOnInit() {
    this.companyInfo = new Empresa();
    this.companyInfo.regimenFiscal = '*';
    this.companyInfo.giro = '*';
    this.companyInfo.tipo = '*';
    this.companyInfo.informacionFiscal.pais = 'México';
    this.errorMessages = [];
      /** recovering folio info**/
      this.route.paramMap.subscribe(route => {
        const rfc = route.get('rfc');
        if (rfc !== '*') {
          this.empresaService.getCompanyByRFC(rfc)
        .subscribe((data:Empresa) => {this.companyInfo = data, this.formInfo.rfc = rfc; },
        (error: HttpErrorResponse) => this.errorMessages.push(error.error.message
          || `${error.statusText} : ${error.message}`));
        }});
    /**** LOADING CAT INFO ****/
    this.catalogsService.getAllGiros().subscribe((giros: Catalogo[]) => this.girosCat = giros,
      (error: HttpErrorResponse) => this.errorMessages.push(error.error.message
        || `${error.statusText} : ${error.message}`));
  }

  sanitize(url: string) {
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }


  public zipCodeInfo(zipcode:String){
    let zc = new String(zipcode);
    if(zc.length>4 && zc.length <6){
      this.colonias = [];
      this.catalogsService.getZipCodeInfo(zipcode).subscribe(
          (data:ZipCodeInfo) => {this.companyInfo.informacionFiscal.estado = data.estado;
          this.companyInfo.informacionFiscal.municipio= data.municipio;this.colonias=data.colonias; 
          this.companyInfo.informacionFiscal.localidad=data.colonias[0];},
          (error: HttpErrorResponse) => console.error(error));
    }
  }

  public onLocation(index:string){
    this.companyInfo.informacionFiscal.localidad = this.colonias[index];
  }

  public onRegimenFiscalSelected(regimen:string){
    this.companyInfo.regimenFiscal= regimen;
  }

  public onGiroSelection(giro:string){
    this.companyInfo.giro =  giro;
  }

  public onLineaSelected(linea:string){
    this.companyInfo.tipo = linea;
  }

  logoUploadListener(event: any): void {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      if (file.size > 200000){
        alert('El archivo demasiado grande, intenta con un archivo mas pequeño.');
      }else {
        reader.readAsDataURL(file);
      reader.onload = () => {
        this.formInfo.logoFileName = file.name;
        this.companyInfo.logotipo = reader.result.toString(); };
      reader.onerror = (error) => {
        this.errorMessages.push('Error parsing image file');
        console.error(error); };
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
        this.companyInfo.llavePrivada = data.substring(data.indexOf('base64') + 7, data.length); };
      reader.onerror = (error) => {
        this.errorMessages.push('Error parsing key file');
        console.error(error); };
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
      this.companyInfo.certificado = data.substring(data.indexOf('base64') + 7, data.length);};
    reader.onerror = (error) => {this.errorMessages.push('Error parsing certificate file')};
    }
  }

  public insertNewCompany(): void {
    this.errorMessages = [];
    this.formInfo.success = '';
    this.errorMessages = this.companiesValidatorService.validarEmpresa(this.companyInfo);
   if (this.errorMessages.length === 0) {
      this.companyInfo.giro = this.girosCat.find(g => g.nombre === this.companyInfo.giro).id.toString();
      console.log(this.companyInfo);
      this.empresaService.insertNewCompany(this.companyInfo)
      .subscribe((empresa: Empresa) => { this.router.navigate([`./pages/operaciones/empresas`]); },
      (error: HttpErrorResponse) => {this.errorMessages.push(error.error.message
        || `${error.statusText} : ${error.message}`);
      });
    }
  }

  public updateCompany():void{
    this.errorMessages = [];
    this.formInfo.success ='';
    this.empresaService.updateCompany(this.companyInfo.informacionFiscal.rfc,this.companyInfo)
    .subscribe((data:Empresa) => {this.router.navigate([`./pages/contablidad/empresas`]);},
    (error : HttpErrorResponse)=>{this.errorMessages.push(error.error.message || `${error.statusText} : ${error.message}`);}
    );
  }

  public inactivateCompany(){
    this.companyInfo.activo = false;
    this.empresaService.updateCompany(this.companyInfo.informacionFiscal.rfc, this.companyInfo)
    .subscribe((data:Empresa) => this.formInfo.success ='la empresa ha sido desactivada satisfactoriamente',
    (error : HttpErrorResponse)=>{this.errorMessages.push(error.error.message || `${error.statusText} : ${error.message}`);});
  }

}
