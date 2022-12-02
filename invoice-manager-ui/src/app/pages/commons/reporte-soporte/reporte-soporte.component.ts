import { Component, OnInit } from '@angular/core';
import { GenericPage } from '../../../models/generic-page';
import { Router } from '@angular/router';
import { UtilsService } from '../../../@core/util-services/utils.service';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { SupportData } from '../../../@core/data/support-data';
import { SupportRequest } from '../../../models/support-request';

@Component({
    selector: 'nt-reporte-soporte',
    templateUrl: './reporte-soporte.component.html',
    styleUrls: ['./reporte-soporte.component.scss'],
})
export class ReporteSoporteComponent implements OnInit {
    public modules: [];
    public filterParams: any = {
        contactEmail: '',
        folio: '',
        since: undefined,
        to: undefined,
        status: '*',
        module: '*',
        supportType: '*',
        supportLevel: '*',
        requestType: '*',
        agent: '',
        page: '0',
        size: '10',
    };
    public page: GenericPage<SupportRequest> = new GenericPage();
    public loading = false;
    public adminView = false;

    constructor(
        private router: Router,
        private utilsService: UtilsService,
        private downloadService: DonwloadFileService,
        private supportService: SupportData
    ) {}

    ngOnInit() {
        this.page.empty = false;
        this.page.totalElements = 500;
        this.modules = JSON.parse(sessionStorage.getItem('user'))?.roles;
        if (
            !this.router.url.includes('/pages/administracion') &&
            !this.router.url.includes('/pages/soporte')
        ) {
            this.filterParams.contactEmail = sessionStorage.getItem('email');
            this.adminView = false;
        } else {
            this.adminView = true;
        }
        this.updateDataTable();
    }

    updateDataTable(currentPage?: number, pageSize?: number) {
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        params.page =
            currentPage !== undefined ? currentPage : this.filterParams.page;
        params.size =
            pageSize !== undefined ? pageSize : this.filterParams.size;
        this.supportService
            .getSoportes(params)
            .subscribe((result: GenericPage<any>) => {
                return (this.page = result);
            });
    }

    redirectToSoporte(id: number) {
        if (
            !this.router.url.includes('pages/administracion') &&
            !this.router.url.includes('pages/soporte')
        ) {
            this.router.navigate([`./pages/solicitud/${id}`]);
        } else {
            this.router.navigate([
                `./pages/${this.router.url.split('/')[2]}/soporte/${id}`,
            ]);
        }
    }

    onChangePageSize(pageSize: number) {
        this.updateDataTable(this.page.number, pageSize);
    }

    downloadReports() {
        const params: any = this.utilsService.parseFilterParms(
            this.filterParams
        );
        params.page = 0;
        params.size = 10000;
        this.supportService.getSoporteReport(params).subscribe((file) => {
            this.downloadService.downloadFile(
                file.data,
                'SoporteReport.xlsx',
                'data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,'
            );
        });
    }
}
