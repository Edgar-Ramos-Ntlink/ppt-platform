import { Component, OnInit } from '@angular/core';
import { ClientsData } from '../../../@core/data/clients-data';
import { GenericPage } from '../../../models/generic-page';
import { Client } from '../../../models/client';
import { DownloadCsvService } from '../../../@core/util-services/download-csv.service'
import { Router, ActivatedRoute } from '@angular/router';
import { UsersData } from '../../../@core/data/users-data';
import { User } from '../../../models/user';

@Component({
  selector: 'ngx-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.scss']
})
export class ClientesComponent implements OnInit {

  public user: User;
  public page: GenericPage<any> = new GenericPage();

  public module: string = 'promotor';
  public filterParams: any = { razonSocial: '', rfc: '', status: '*', promotor: '', page: '0', size: '10' };

  constructor(
    private userService: UsersData,
    private clientService: ClientsData,
    private donwloadService: DownloadCsvService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.module = this.router.url.split('/')[2];

    this.route.queryParams
      .subscribe(params => {

        this.filterParams = { ...this.filterParams, ...params };

        switch (this.module) {
          case 'promotor':
            this.userService.getUserInfo().then((user) => {
              this.filterParams.promotor = user.email;
              this.updateDataTable();
            });
            break;
          case 'operaciones':
            this.updateDataTable();
            break;
          default:
            this.updateDataTable();
            break;
        }
      });
  }


  public updateDataTable(currentPage?: number, pageSize?: number) {

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

    params.page = currentPage !== undefined ? currentPage : this.filterParams.page;
    params.size = pageSize !== undefined ? pageSize : this.filterParams.size;

    switch (this.module) {
      case 'promotor':
        this.router.navigate([`./pages/promotor/clientes`],
          { queryParams: params });
        break;
      case 'operaciones':
        this.router.navigate([`./pages/operaciones/clientes`],
          { queryParams: params });
        break;
      default:
        this.router.navigate([`./pages/promotor/clientes`],
          { queryParams: params });
    }

    this.clientService.getClients(params)
      .subscribe((result: GenericPage<any>) => this.page = result);

  }

  public onChangePageSize(pageSize: number) {
    this.updateDataTable(this.page.number, pageSize);
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
    this.clientService.getClients(params).subscribe((result: GenericPage<Client>) => {
      this.donwloadService.exportCsv(result.content.map(r => r.informacionFiscal), 'Clientes');
    });
  }

  public redirectToCliente(rfc: string) {
    this.router.navigate([`./pages/${this.module}/cliente/${rfc}`]);
  }

}
