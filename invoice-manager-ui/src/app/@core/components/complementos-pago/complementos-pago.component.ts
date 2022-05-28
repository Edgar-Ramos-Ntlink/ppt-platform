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

    public redirectToChildCfdi(folio: string) {
        this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
    }

    public redirectToCfdi(folio: string) {
        this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
    }
}
