import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { RegimenFiscal } from '../../../models/catalogos/regimen-fiscal';
import { UsoCfdi } from '../../../models/catalogos/uso-cfdi';
import { AppState } from '../../../reducers';
import { updateReceptor, updateReceptorAddress } from '../../core.actions';
import { CatalogsData } from '../../data/catalogs-data';
import { Receptor } from '../../models/cfdi/receptor';

@Component({
    selector: 'nt-receptor',
    templateUrl: './receptor.component.html',
    styleUrls: ['./receptor.component.scss'],
})
export class ReceptorComponent implements OnInit {
    @Input() public receptor: Receptor;
    @Input() public allowEdit: Boolean;
    @Input() public direccion: string;

    // catalogs
    public usoCfdiCat: UsoCfdi[] = [];
    public regimenes: RegimenFiscal[] = [];

    constructor(
        private catalogsService: CatalogsData,
        private store: Store<AppState>
    ) {}

    ngOnInit(): void {
        this.catalogsService
            .getAllUsoCfdis()
            .then((cat) => (this.usoCfdiCat = cat));
        this.catalogsService
            .getAllRegimenFiscal()
            .then((reg) => (this.regimenes = reg));
    }

    public addressChange(address:string) {
        this.store.dispatch(updateReceptorAddress({ address}));
    }

    public updateRegimenFiscal(value: string) {
        const receptor = { ...this.receptor, regimenFiscalReceptor: value };
        this.store.dispatch(updateReceptor({ receptor }));
    }

    public updateRazonSocial(value: string) {
        const receptor = { ...this.receptor, nombre: value.trim().toUpperCase() };
        this.store.dispatch(updateReceptor({ receptor }));
    }

    public updateUsoCfdi(value: string) {
        const receptor = { ...this.receptor, usoCfdi: value };
        this.store.dispatch(updateReceptor({ receptor }));
    }

    public updateDomicilioFiscal(cp: string) {
        const receptor = { ...this.receptor, domicilioFiscalReceptor: cp };
        this.store.dispatch(updateReceptor({ receptor }));
    }
}
