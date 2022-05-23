import { Component, Input, OnInit } from "@angular/core";
import { NbDialogRef, NbToastrService } from "@nebular/theme";
import { ClaveUnidad } from "../../../../models/catalogos/clave-unidad";
import { ClaveProductoServicio } from "../../../../models/catalogos/producto-servicio";
import { CatalogsData } from "../../../data/catalogs-data";
import { Concepto } from "../../../models/cfdi/concepto";
import { CfdiValidatorService } from "../../../util-services/cfdi-validator.service";

@Component({
  selector: "nt-concepto",
  templateUrl: "./concepto.component.html",
  styleUrls: ["./concepto.component.scss"],
})
export class ConceptoComponent implements OnInit {
  @Input() public concepto: Concepto;

  public formInfo = {
    prodServ: "*",
    unidad: "*",
    claveProdServ: "",
    claveProdServFlag: false,
    iva: true,
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
      .then((unidadCat) => (this.claveUnidadCat = unidadCat))
      .then(() => (this.formInfo.unidad = "E48"));
  }

  public onSelectUnidad(clave: string) {
    if (clave !== "*") {
      this.concepto.claveUnidad = clave;
      this.concepto.unidad = this.claveUnidadCat.find(
        (u) => u.clave === clave
      ).nombre;
    }
  }

  public onClaveSelected(clave: ClaveProductoServicio) {
    this.concepto.claveProdServ = clave.clave;
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
    window.open("http://pys.sat.gob.mx/PyS/catPyS.aspx", "_blank");
  }

  public onClaveProdServSelected(clave: string) {
    this.concepto.claveProdServ = clave;
  }

  public ivaCheckboxChange() {
    this.formInfo.iva = !this.formInfo.iva;
  }

  exit() {
    this.ref.close();
  }

  submit() {
    const concepto = this.cfdiValidator.buildConcepto(
      { ...this.concepto },
      this.formInfo.iva,
      false
    );
    const errors = this.cfdiValidator.validarConcepto(concepto);
    if (errors.length > 0) {
      errors.forEach((e) => this.toastrService.warning("Datos faltantes", e));
      return;
    } else {
      this.ref.close(concepto);
    }
  }
}
