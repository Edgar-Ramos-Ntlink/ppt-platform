import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { CompaniesData } from '../../@core/data/companies-data';
import { FilesData } from '../../@core/data/files-data';
import { GenericPage } from '../../models/generic-page';

interface CardSettings {
    title: string;
    imageSrc: string;
    description: string;
    linkUrl: string;
}

@Component({
    selector: 'ngx-dashboard',
    styleUrls: ['./dashboard.component.scss'],
    templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
    constructor(
        private companiesService: CompaniesData,
        private resourcesService: FilesData
    ) {}

    public cards: CardSettings[] = [];

    ngOnInit(): void {
        this.companiesService
            .getCompanies({
                activo: true,
                linea: 'A',
                size: 12,
                page: this.randomIntFromInterval(0, 3),
            })
            .pipe(
                map((page: GenericPage<any>) => {
                    let result = page.content.map((e) =>
                        Object.assign(
                            {},
                            {
                                title: e.NOMBRE_CORTO,
                                imageSrc: `/api/empresas/${e.RFC}/logo`,
                                description: e.GIRO,
                                linkUrl: (e.PAGINA_WEB !== null && e.PAGINA_WEB !== undefined) ? e.PAGINA_WEB : 'NO URL'
                            }
                        )
                    );
                    return result;
                })
            )
            .subscribe((result) => (this.cards = result));
    }

    public goToLink(link: string) {
        console.log('Redirecting to :', link);
        window.open(link, '_blank');
    }

    private randomIntFromInterval(min, max) {
        // min and max included
        return Math.floor(Math.random() * (max - min + 1) + min);
    }
}
