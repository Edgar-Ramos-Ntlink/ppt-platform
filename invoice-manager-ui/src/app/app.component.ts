import { Component, OnInit } from '@angular/core';
import { CatalogsData } from './@core/data/catalogs-data';

@Component({
    selector: 'ngx-app',
    template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit {
    constructor() {}

    ngOnInit(): void {
        
    }
}
