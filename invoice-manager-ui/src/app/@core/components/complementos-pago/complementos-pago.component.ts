import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { invoice } from '../../core.selectors';
import { Factura } from '../../models/factura';

@Component({
    selector: 'nt-complementos-pago',
    templateUrl: './complementos-pago.component.html',
    styleUrls: ['./complementos-pago.component.scss'],
})
export class ComplementosPagoComponent implements OnInit {
    public factura: Factura;

    constructor(private store: Store<AppState>, private router: Router) {}

    ngOnInit(): void {
        this.store
            .pipe(select(invoice))
            .subscribe((fact) => (this.factura = fact));
    }


    public redirectToCfdi(folio: string) {
        const url = this.router.url;
        const redirectUrl = `.${url.substring(0,url.lastIndexOf('/'))}/${folio}`
        this.router.navigate([redirectUrl]);
    }
}
