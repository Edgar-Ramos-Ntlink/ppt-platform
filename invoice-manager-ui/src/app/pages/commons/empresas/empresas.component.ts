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
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';

@Component({
  selector: 'ngx-empresas',
  templateUrl: './empresas.component.html',
  styleUrls: ['./empresas.component.scss']
})
export class EmpresasComponent implements OnInit {

  public girosCat: Catalogo[];
  public loading = false;

  public page: GenericPage<any> = new GenericPage();
  public pageSize = '10';
  public filterParams: any = { activo: '*', linea: '*', giro: '*', razonSocial: '', rfc: '', actividadSAT: '', registroPatronal: '*', representanteLegal: '*', impuestoEstatal: '*', page: '0', size: '10' };
  public module: string = 'operaciones';
  constructor(private router: Router,
    private companyService: CompaniesData,
    private route: ActivatedRoute,
    private catalogsService: CatalogsData,
    private utilsService: UtilsService,
    private downloadService: DonwloadFileService) { }

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
    this.loading = true;
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
      case 'administracion':
        this.router.navigate([`./pages/administracion/empresas`],
          { queryParams: params });
        break;
      default:
        this.router.navigate([`./pages/operaciones/empresas`],
          { queryParams: params });
    }

    this.companyService.getCompanies(params).subscribe((result: GenericPage<any>) => {
      this.page = result
      this.loading = false
    }, error => { console.error(error); this.loading = false });
  }


  public onChangePageSize(pageSize: number) {
    this.updateDataTable(this.page.number, pageSize);
  }

  public onCompanySelected(linea: string) {
    this.filterParams.linea = '*';
  }

  public newCompany() {
    this.router.navigate([`./pages/operaciones/empresa/*`])
  }

  public async downloadHandler() {
    const params: any = { ... this.filterParams };
    params.page = 0;
    params.size = 2000;
    this.loading = true;
    console.log("Start LOADER");
    try {
      const result = await this.companyService.getCompaniesReport(params).toPromise();
      this.downloadService.downloadFile(result.data, 'ReporteEmpresas.xlsx', 'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,');
    } catch (error) {
      console.error(error);
    } 
    console.log("Stop LOADER");
    this.loading = false;

  }

  public redirectToEmpresa(rfc: string) {
    this.router.navigate([`./pages/${this.module}/empresa/${rfc}`])
  }
}
