import { Component, OnInit } from '@angular/core';
import { ClientsData } from '../../../@core/data/clients-data';
import { GenericPage } from '../../../models/generic-page';
import { Router, ActivatedRoute } from '@angular/router';
import { UtilsService } from '../../../@core/util-services/utils.service';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';

@Component({
    selector: 'ngx-clientes',
    templateUrl: './clientes.component.html',
    styleUrls: ['./clientes.component.scss'],
})
export class ClientesComponent implements OnInit {
    public loading: boolean = false;
    public page: GenericPage<any> = new GenericPage();

    public module: string = 'promotor';
    public filterParams: any = {
        razonSocial: '',
        rfc: '',
        status: '*',
        promotor: '',
        page: '',
        size: '10',
    };

    constructor(
        private downloadService: DonwloadFileService,
        private clientService: ClientsData,
        private route: ActivatedRoute,
        private utilsService: UtilsService,
        private router: Router
    ) {}

    ngOnInit() {
        this.module = this.router.url.split('/')[2];

        this.route.queryParams.subscribe((params) => {
            if (!this.utilsService.compareParams(params, this.filterParams)) {
                this.filterParams = { ...this.filterParams, ...params };
                switch (this.module) {
                    case 'promotor':
                        this.filterParams.promotor =
                            sessionStorage.getItem('email');
                        this.updateDataTable(
                            this.filterParams.page,
                            this.filterParams.size
                        );

                        break;
                    case 'operaciones':
                        this.updateDataTable(
                            this.filterParams.page,
                            this.filterParams.size
                        );
                        break;
                    case 'administracion':
                        this.updateDataTable(
                            this.filterParams.page,
                            this.filterParams.size
                        );
                        break;
                    default:
                        this.updateDataTable(
                            this.filterParams.page,
                            this.filterParams.size
                        );
                        break;
                }
            }
        });
    }

    public updateDataTable(currentPage?: number, pageSize?: number) {
        this.loading = true;
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        params.page =
            currentPage !== undefined ? currentPage : this.filterParams.page;
        params.size =
            pageSize !== undefined ? pageSize : this.filterParams.size;

        if (this.module) {
            this.router.navigate([`./pages/${this.module}/clientes`], {
                queryParams: params,
            });
        } else {
            this.router.navigate([`./pages/promotor/clientes`], {
                queryParams: params,
            });
        }

        this.clientService.getClients(params).subscribe(
            (result: GenericPage<any>) => {
                this.page = result;
                this.loading = false;
            },
            (error) => {
                console.log(error);
                this.loading = false;
            }
        );
    }

    public onChangePageSize(pageSize: number) {
        this.updateDataTable(this.page.number, pageSize);
    }

    public async downloadHandler() {
        const params: any = { ...this.filterParams };
        params.page = 0;
        params.size = 2000;
        this.loading = true;
        try {
            const result = await this.clientService
                .getClientsReport(params)
                .toPromise();
            this.downloadService.downloadFile(
                result.data,
                'ReporteClientes.xlsx',
                'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,'
            );
        } catch (error) {
            console.error(error);
        }
        this.loading = false;
    }

    public redirectToCliente(id: number) {
        this.router.navigate([`./pages/${this.module}/cliente/${id}`]);
    }
}
