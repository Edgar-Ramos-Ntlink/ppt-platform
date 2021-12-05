import { Component, OnInit } from '@angular/core';

import { CompaniesData } from '../../../@core/data/companies-data';
import { GenericPage } from '../../../models/generic-page';
import { DownloadCsvService } from '../../../@core/util-services/download-csv.service'
import { Router, ActivatedRoute } from '@angular/router';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { Observable } from 'rxjs';
import { Empresa } from '../../../models/empresa';
import { map } from 'rxjs/operators';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { UtilsService } from '../../../@core/util-services/utils.service';

@Component({
  selector: 'ngx-empresas',
  templateUrl: './empresas.component.html',
  styleUrls: ['./empresas.component.scss']
})
export class EmpresasComponent implements OnInit {

  public girosCat: Catalogo[];

  public page: GenericPage<any> = new GenericPage();
  public pageSize = '10';
  public filterParams: any = { razonSocial: '', rfc: '', linea: '', page: '', size: '10' };
  public module: string = 'operaciones';
  constructor(private router: Router,
    private companyService: CompaniesData,
    private route: ActivatedRoute,
    private catalogsService: CatalogsData,
    private utilsService: UtilsService,
    private donwloadService: DownloadCsvService) { }

  ngOnInit() {
    this.module = this.router.url.split('/')[2];
    this.route.queryParams
      .subscribe(params => {

        if (!this.utilsService.compareParams(params, this.filterParams)) {
          this.filterParams = { ...this.filterParams, ...params };

          this.catalogsService.getAllGiros()
            .then(cat => this.girosCat = cat)
            .then(() => this.updateDataTable());
        }
      });




  }

  
  public updateDataTable(currentPage?: number, pageSize?: number) {

    const params: any = this.utilsService.parseFilterParms(this.filterParams);

    params.page = currentPage !== undefined ? currentPage : this.filterParams.page;
    params.size = pageSize !== undefined ? pageSize : this.filterParams.size;

    switch (this.module) {
      case 'operaciones':
        this.router.navigate([`./pages/operaciones/empresas`],
          { queryParams: params });
        break;
      case 'legal':
        this.router.navigate([`./pages/legal/empresas`],
          { queryParams: params });
        break;
      case 'tesoreria':
          this.router.navigate([`./pages/tesoreria/empresas`],
            { queryParams: params });
          break;
      case 'contabilidad':
        this.router.navigate([`./pages/contabilidad/empresas`],
          { queryParams: params });
        break;
      default:
        this.router.navigate([`./pages/operaciones/empresas`],
          { queryParams: params });
    }

    this.companyService.getCompanies(params).subscribe((result: GenericPage<any>) => this.page = result);
  }


  public onChangePageSize(pageSize: number) {
    this.updateDataTable(this.page.number, pageSize);
  }

  public onCompanySelected(tipo: string) {
    if (tipo === '*') {
      this.filterParams.linea = '';
    } else {
      this.filterParams.linea = tipo;
    }
  }

  public newCompany() {
    this.router.navigate([`./pages/operaciones/empresa/*`])
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
    this.companyService.getCompanies(params).subscribe(result => {
      this.donwloadService.exportCsv(result.content, 'Empresas')
    });
  }

  public redirectToEmpresa(rfc: string) {
    this.router.navigate([`./pages/${this.module}/empresa/${rfc}`])
  }
}
