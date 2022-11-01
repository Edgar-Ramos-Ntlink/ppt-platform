import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
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

    public conceptForm = new FormGroup({
        
        descripcion: new FormControl('', [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(1000),
          Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        unidad: new FormControl('', [
          Validators.required,
          Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        claveUnidad: new FormControl('E48', [
          Validators.required,
          Validators.pattern(AppConstants.UNIT_CATALOG_PATTERN),
        ]),
        cantidad: new FormControl('', [
          Validators.required,
          Validators.pattern(AppConstants.SIX_DECIMAL_DIGITS_AMOUNT_PATTERN),
        ]),
        valorUnitario: new FormControl('', [
          Validators.required,
          Validators.pattern(AppConstants.SIX_DECIMAL_DIGITS_AMOUNT_PATTERN),
        ]),
        claveProdServ: new FormControl('', [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(8),
          Validators.pattern(AppConstants.CLAVE_PROD_SERV_PATTERN),
        ]),
        claveProdServDesc: new FormControl('',[
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        objetoImp: new FormControl('', [
          Validators.maxLength(2),
          Validators.pattern(AppConstants.OBJ_IMP_PATTERN)
        ]),
        impuesto: new FormControl('IVA0.16', [
            Validators.pattern(AppConstants.IMPUESTO_PATTERN)
          ]),
      }); 

    public formInfo = {
        claveProdServ: undefined,
        claveProdServFlag: false,
    };

    public prodServCat: ClaveProductoServicio[] = [];
    public claveUnidadCat: ClaveUnidad[] = [];

    constructor(
        protected ref: NbDialogRef<ConceptoComponent>,
        private catalogsService: CatalogsData,
        private cfdiValidator: CfdiValidatorService,
        private toastrService: NbToastrService
    ) {}

    ngOnInit(): void {
        this.catalogsService
            .getClaveUnidadCatalog()
            .then((unidadCat) => (this.claveUnidadCat = unidadCat));
            
            this.concepto.impuesto = this.concepto.impuestos.length > 0 ? `IVA_${this.concepto.impuestos[0].traslados[0].tasaOCuota.toString()}` : 'IVA0.16';
            Object.keys(this.conceptForm.controls).forEach(key => this.conceptForm.controls[key].setValue((this.concepto[key]!= undefined && this.concepto[key]!= null ) ? this.concepto[key] : '')); 
            if(this.concepto.impuestos.length>0){
                this.formInfo.claveProdServ = {clave : this.concepto.claveProdServ, descripcion : `${this.concepto.claveProdServ} - ${this.concepto.claveProdServDesc}`};
            }
        }

    public onSelectUnidad(clave: string) {
        if (clave !== '*') {
            this.concepto.claveUnidad = clave;
            this.concepto.unidad = this.claveUnidadCat.find(
                (u) => u.clave === clave
            ).nombre;
        }
    }

    public onClaveSelected(clave: ClaveProductoServicio) {
        console.log('onClaveSelected', clave)
        this.conceptForm.controls['claveProdServ'].setValue(clave.clave);
        if(clave.descripcion){
            var splitted = clave.descripcion.split('-', 2);
        if (splitted.length > 1) {
            this.conceptForm.controls['claveProdServDesc'].setValue(splitted[1].trim());
        }
        }
    }

    public onCleanSearch(){
        this.formInfo.claveProdServ = undefined;
        this.conceptForm.controls['claveProdServ'].setValue(undefined);
        this.conceptForm.controls['claveProdServDesc'].setValue('');
    }

    public async buscarClaveProductoServicio(claveProdServ: string) {
        const value = +claveProdServ;
        try {
            this.formInfo.claveProdServFlag = false;
            if (isNaN(value)) {
                if (claveProdServ.length > 5) {
                    this.prodServCat =
                        await this.catalogsService.getProductoServiciosByDescription(
                            claveProdServ
                        );
                } else {
                    this.prodServCat = [];
                }
            } else {
                if (claveProdServ.length === 8) {
                    this.prodServCat =
                        await this.catalogsService.getProductoServiciosByClave(
                            claveProdServ
                        );
                } else {
                    if (claveProdServ.length < 8 && claveProdServ.length > 3) {
                        this.formInfo.claveProdServFlag = true;
                    }
                    this.prodServCat = [];
                }
            }
        } catch (error) {
            this.toastrService.warning(error?.message);
        }
    }

    openSatCatalog() {
        window.open('http://pys.sat.gob.mx/PyS/catPyS.aspx', '_blank');
    }

    public onClaveProdServSelected(clave: string) {
        this.concepto.claveProdServ = clave;
    }

    public onSelectedImpuesto(iva: string) {
        console.log(iva)
    }

    exit() {
        this.ref.close();
    }

    submit() {
        const concepto = this.cfdiValidator.buildConcepto(
            { ... this.conceptForm.value });
        
        this.ref.close(concepto);
    }

    get descripcion() {return this.conceptForm.get("descripcion")!}
    get unidad() {return this.conceptForm.get("unidad")!} 
    get claveUnidad() {return this.conceptForm.get("claveUnidad")!} 
    get cantidad() {return this.conceptForm.get("cantidad")!} 
    get valorUnitario() {return this.conceptForm.get("valorUnitario")!} 
    get claveProdServ() {return this.conceptForm.get("claveProdServ")!} 
    get objetoImp() {return this.conceptForm.get("objetoImp")!} 
    get impuesto() {return this.conceptForm.get("impuesto")!} 
   
}
