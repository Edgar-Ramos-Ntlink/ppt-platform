import { Component, Input, OnInit } from '@angular/core';
import {
    FormBuilder,
    FormControl,
    FormGroup,
    FormGroupDirective,
    Validators,
} from '@angular/forms';
import { NbDialogRef, NbToastrService } from '@nebular/theme';
import { AppConstants } from '../../../../models/app-constants';
import { ClaveUnidad } from '../../../../models/catalogos/clave-unidad';
import { ClaveProductoServicio } from '../../../../models/catalogos/producto-servicio';
import { CatalogsData } from '../../../data/catalogs-data';
import { Concepto } from '../../../models/cfdi/concepto';
import { CfdiValidatorService } from '../../../util-services/cfdi-validator.service';

@Component({
    selector: 'nt-concepto',
    templateUrl: './concepto.component.html',
    styleUrls: ['./concepto.component.scss'],
})
export class ConceptoComponent implements OnInit {
    @Input() public concepto: Concepto;
    public initClaveProdServ: ClaveProductoServicio;
    public prodServCat: ClaveProductoServicio[] = [];
    public claveUnidadCat: ClaveUnidad[] = [];

    public conceptForm: FormGroup;

    constructor(
        protected ref: NbDialogRef<ConceptoComponent>,
        private catalogsService: CatalogsData,
        private cfdiValidator: CfdiValidatorService,
        private toastrService: NbToastrService,
        private formBuilder: FormBuilder
    ) {
        this.conceptForm = this.formBuilder.group({
            descripcion: [
                '',
                [
                    Validators.required,
                    Validators.minLength(5),
                    Validators.maxLength(1000),
                    Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
                ],
            ],
            unidad: [
                '',
                [
                    Validators.required,
                    Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
                ],
            ],
            claveUnidad: [
                'E48',
                [
                    Validators.required,
                    Validators.pattern(AppConstants.UNIT_CATALOG_PATTERN),
                ],
            ],
            cantidad: [
                '',
                [
                    Validators.required,
                    Validators.pattern(
                        AppConstants.SIX_DECIMAL_DIGITS_AMOUNT_PATTERN
                    ),
                ],
            ],
            valorUnitario: [
                '',
                [
                    Validators.required,
                    Validators.pattern(
                        AppConstants.SIX_DECIMAL_DIGITS_AMOUNT_PATTERN
                    ),
                ],
            ],
            claveProdServ: [
                '',
                [
                    Validators.required,
                    Validators.minLength(8),
                    Validators.maxLength(9),
                    Validators.pattern(AppConstants.CLAVE_PROD_SERV_PATTERN),
                ],
            ],
            claveProdServDesc: [
                '',
                [
                    Validators.minLength(4),
                    Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
                ],
            ],
            objetoImp: [
                '',
                [
                    Validators.maxLength(2),
                    Validators.pattern(AppConstants.OBJ_IMP_PATTERN),
                ],
            ],
            impuesto: [
                'IVA_0.16',
                [Validators.pattern(AppConstants.IMPUESTO_PATTERN)],
            ],
        });
    }

    ngOnInit(): void {
        this.catalogsService
            .getClaveUnidadCatalog()
            .then((unidadCat) => (this.claveUnidadCat = unidadCat));

        this.concepto.impuesto =
            this.concepto.impuestos && this.concepto.impuestos.length > 0
                ? `IVA_${this.concepto.impuestos[0].traslados[0].tasaOCuota.toString()}`
                : 'IVA_0.16';

        Object.keys(this.conceptForm.controls).forEach((key) =>
            this.conceptForm.controls[key].setValue(
                this.concepto[key] != undefined && this.concepto[key] != null
                    ? this.concepto[key]
                    : ''
            )
        );
        if (this.concepto?.claveProdServ) {
            this.initClaveProdServ = new ClaveProductoServicio(
                this.concepto.claveProdServ,
                `${this.concepto.claveProdServ} - ${this.concepto.claveProdServDesc}`
            );
        }
    }

    public onSelectUnidad(clave: string) {
        if (clave !== '*') {
            this.conceptForm.controls['claveUnidad'].setValue(clave);
            this.conceptForm.controls['unidad'].setValue(
                this.claveUnidadCat.find((u) => u.clave === clave).nombre
            );
        }
    }

    public onClaveSelected(clave: ClaveProductoServicio) {
        if (clave?.clave) {
            this.conceptForm.controls['claveProdServ'].setValue(clave.clave);
            if (clave.descripcion) {
                const splitted = clave.descripcion.split('-', 2);
                if (splitted.length > 1) {
                    this.conceptForm.controls['claveProdServDesc'].setValue(
                        splitted[1].trim()
                    );
                }
            }
        }
    }

    public onCleanSearch() {
        this.initClaveProdServ = undefined;
        this.conceptForm.controls['claveProdServ'].setValue(undefined);
        this.conceptForm.controls['claveProdServDesc'].setValue('');
    }

    public async buscarClaveProductoServicio(claveProdServ: string) {
        const value = +claveProdServ;
        try {
            if (isNaN(value)) {
                this.conceptForm.controls['claveProdServDesc'].setValue(
                    claveProdServ
                );
                if (claveProdServ.length >= 5) {
                    this.prodServCat =
                        await this.catalogsService.getProductoServiciosByDescription(
                            claveProdServ
                        );
                } else {
                    this.prodServCat = [];
                }
            } else {
                this.conceptForm.controls['claveProdServ'].setValue(
                    claveProdServ
                );
                if (claveProdServ.length === 8) {
                    this.prodServCat =
                        await this.catalogsService.getProductoServiciosByClave(
                            claveProdServ
                        );
                } else {
                    this.prodServCat = [];
                }
            }
        } catch (error) {
            this.toastrService.warning(error?.message);
        }
    }

    public openSatCatalog() {
        window.open('http://pys.sat.gob.mx/PyS/catPyS.aspx', '_blank');
    }

    public exit() {
        this.ref.close();
    }

    public submit(concept: FormGroupDirective) {
        const concepto = this.cfdiValidator.buildConcepto({
            ...concept.form.value,
        });
        this.ref.close(concepto);
    }

    get descripcion() {
        return this.conceptForm.get('descripcion')!;
    }
    get unidad() {
        return this.conceptForm.get('unidad')!;
    }
    get claveUnidad() {
        return this.conceptForm.get('claveUnidad')!;
    }
    get cantidad() {
        return this.conceptForm.get('cantidad')!;
    }
    get valorUnitario() {
        return this.conceptForm.get('valorUnitario')!;
    }
    get claveProdServ() {
        return this.conceptForm.get('claveProdServ')!;
    }
    get claveProdServDesc() {
        return this.conceptForm.get('claveProdServDesc')!;
    }
    get objetoImp() {
        return this.conceptForm.get('objetoImp')!;
    }
    get impuesto() {
        return this.conceptForm.get('impuesto')!;
    }
}
