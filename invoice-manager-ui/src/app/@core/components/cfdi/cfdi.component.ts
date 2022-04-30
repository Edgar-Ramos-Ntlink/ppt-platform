import { Component, OnInit, Input, TemplateRef } from "@angular/core";
import { CatalogsData } from "../../data/catalogs-data";
import { Catalogo } from "../../../models/catalogos/catalogo";
import { CfdiData } from "../../data/cfdi-data";
import { HttpErrorResponse } from "@angular/common/http";
import { NbDialogService, NbToastrService } from "@nebular/theme";
import { DonwloadFileService } from "../../util-services/download-file-service";
import { FilesData } from "../../data/files-data";
import { Router } from "@angular/router";
import { InvoicesData } from "../../data/invoices-data";
import { AppState } from "../../../reducers";
import { select, Store } from "@ngrx/store";
import { cfdi, invoice } from "../../core.selectors";
import { Cfdi } from "../../models/cfdi/cfdi";
import { updateCfdi, updateInvoice } from "../../core.actions";
import { Factura } from "../../models/factura";
import { NtError } from "../../models/nt-error";

@Component({
  selector: "nt-cfdi",
  templateUrl: "./cfdi.component.html",
  styleUrls: ["./cfdi.component.scss"],
})
export class CfdiComponent implements OnInit {
  
  @Input() public allowEdit: Boolean;

  public factura: Factura;
  public cfdi:Cfdi;

  public loading: boolean = false;

  public payTypeCat: Catalogo[] = [];

  constructor(
    private catalogsService: CatalogsData,
    private filesService: FilesData,
    private downloadService: DonwloadFileService,
    private invoiceService: InvoicesData,
    private toastrService: NbToastrService,
    private router: Router,
    private store: Store<AppState>,
  ) {}

  ngOnInit() {
    // catalogs info
    this.initVariables();
    this.store.pipe(select(invoice)).subscribe((fact) => (this.factura = fact));
    this.store.pipe(select(cfdi)).subscribe((cfdi)=>this.cfdi = cfdi);
  }

  initVariables() {
    this.catalogsService.getFormasPago().then((cat) => (this.payTypeCat = cat));
    this.loading = false;
  }

  onPayMethodChange(clave: string) {
    const cfdi = { ...this.cfdi };
    if (clave === "PPD") {
      cfdi.formaPago = "99";
    } else {
      cfdi.formaPago = "01";
    }
    cfdi.metodoPago = clave;
    this.store.dispatch(updateCfdi({ cfdi }));
  }

  onPayWayChange(clave: string) {
    const cfdi = { ...this.cfdi };
    cfdi.formaPago = clave;
    this.store.dispatch(updateCfdi({ cfdi }));
  }

  onCoinChange(moneda: string) {
    const cfdi = { ...this.cfdi };
    cfdi.moneda = moneda;
    if (moneda === "MXN") {
      cfdi.tipoCambio = 1.0;
    }
    this.store.dispatch(updateCfdi({ cfdi }));
  }

  updateCfdi() {
    this.loading = true;
    this.invoiceService.updateInvoice(this.factura).subscribe(invoice=>{
        this.loading = false;
        this.store.dispatch(updateInvoice({ invoice }));
    },
      (error: NtError) => {
        this.loading = false;
        this.toastrService.danger(error?.message,"Error en la actualizacion")
      }
    );
  }

  public async downloadPdf() {
    this.filesService
      .getFacturaFile(this.factura.folio, "PDF")
      .subscribe((file) =>
        this.downloadService.downloadFile(
          file.data,
          `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.pdf`,
          "application/pdf;"
        )
      );
  }
  public async downloadXml() {
    this.filesService
      .getFacturaFile(this.factura.folio, "XML")
      .subscribe((file) =>
        this.downloadService.downloadFile(
          file.data,
          `${this.factura.folio}-${this.factura.rfcEmisor}-${this.factura.rfcRemitente}.xml`,
          "text/xml;charset=utf8;"
        )
      );
  }

  public redirectToCfdi(folio: string) {
    this.invoiceService
      .getInvoiceByFolio(folio)
      .toPromise()
      .then((fact) =>
        this.router.navigate([`./pages/promotor/precfdi/${fact.folio}`])
      );
  }

  public redirectToChildCfdi(folio: string) {
    this.router.navigate([`./pages/promotor/precfdi/${folio}`]);
  }
}
