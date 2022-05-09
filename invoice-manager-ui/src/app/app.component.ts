import { Component, OnInit } from '@angular/core';
import { CatalogsData } from './@core/data/catalogs-data';

@Component({
    selector: 'ngx-app',
    template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit {
    constructor(private catalogsService: CatalogsData) {}

    ngOnInit(): void {
        this.catalogsService
            .getStatusPago()
            .then((cat) =>
                console.log('payment status cat has been loaded : ', cat)
            );
        this.catalogsService
            .getStatusValidacion()
            .then((cat) =>
                console.log('invoice status cat  has been loaded : ', cat)
            );
    }
}
