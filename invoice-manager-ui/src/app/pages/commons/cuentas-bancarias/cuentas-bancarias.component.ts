import { Component, OnInit } from '@angular/core';
import { GenericPage } from '../../../models/generic-page';
import { Router, ActivatedRoute } from '@angular/router';
import { UtilsService } from '../../../@core/util-services/utils.service';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { Empresa } from '../../../models/empresa';
import { CompaniesData } from '../../../@core/data/companies-data';
import { HttpErrorResponse } from '@angular/common/http';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { map } from 'rxjs/operators';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
@Component({
  selector: 'ngx-cuentas-bancarias',
  templateUrl: './cuentas-bancarias.component.html',
  styleUrls: ['./cuentas-bancarias.component.scss']
})
export class CuentasBancariasComponent implements OnInit {

  public page: GenericPage<any> = new GenericPage();
  public pageSize = '10';
  public currentPage = '0';
  public filterParams: any = { banco: '', empresa: '', cuenta: '',clabe:'', page: '', size: '10', razonSocial:'' };

  public module: string = 'tesoreria';
  public girosCat: Catalogo[] = [];
  public companiesCat: Empresa[] = [];
  public banksCat: Catalogo[] = [];

  public companyInfo: Empresa;

  listEmpresasMatch: any[];
  empresasRfc: any[];
  empresasRazonSocial: any[];

  constructor(
    private router: Router,
    private utilsService: UtilsService,
    private route: ActivatedRoute,
    private accountsService: CuentasData,
    private companiesService: CompaniesData,
    private catalogsService: CatalogsData,
    private downloadService: DonwloadFileService,
    ) {
    
   }

  ngOnInit() {
    this.module = this.router.url.split('/')[2];
    this.route.queryParams
      .subscribe(params => {
        if (!this.utilsService.compareParams(params, this.filterParams)) {
          this.filterParams = { ...this.filterParams, ...params };
          this.catalogsService.getAllGiros().then(cat => this.girosCat = cat);
          this.catalogsService.getBancos().then(banks => this.banksCat = banks);

          if(params.empresa){    
            this.companiesService.getCompanyByRFC(params.empresa) .subscribe((company: Empresa) => { this.filterParams.empresarazon = company.razonSocial;});      
          }
          this.updateDataTable();
        }
      });
  }

  public updateDataTable(currentPage?: number, pageSize?: number) {
    const params: any = this.utilsService.parseFilterParms(this.filterParams);
    params.page = currentPage !== undefined ? currentPage : this.filterParams.page;
    params.size = pageSize !== undefined ? pageSize : this.filterParams.size;
    this.router.navigate([`./pages/bancos/cuentas-bancarias`],
    { queryParams: params });

  this.accountsService.getCuentasByParams(params.page,params.size,params).subscribe((result: GenericPage<any>) => this.page = result);
  }

  public redirectToEmpresa(empresa: string,cuenta:string) {
    this.router.navigate([`./pages/${this.module}/cuenta-bancaria/${empresa}/${cuenta}`])
  }
  public redirectToEmpresaRegistry() {
    this.router.navigate([`./pages/${this.module}/cuenta-bancaria/*/*`])
  }

  public downloadHandler() {
    const params: any = {};
    /* Parsing logic */
    for (const key in this.filterParams) {
      if (this.filterParams[key] !== undefined) {
        const value: string = this.filterParams[key];
        if (value !== null && value.length > 0) {
          params[key] = value;
        }
      }
    }
    params.page = 0;
    params.size = 10000;
    this.accountsService.getCuentasReport(params.page,params.size,params).subscribe(result => {
      this.downloadService.downloadFile(result.data,'ReporteCuentasBancarias.xlsx','data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,');
    });
  }
  public onChangePageSize(pageSize: number) {
    this.updateDataTable(this.page.number, pageSize);
  }

}
