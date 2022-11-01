import { Component, OnInit, Input } from "@angular/core";
import { CfdiData } from "../../data/cfdi-data";
import { NbDialogService } from "@nebular/theme";
import { Cfdi } from "../../models/cfdi/cfdi";
import { Concepto } from "../../models/cfdi/concepto";
import { ConceptoComponent } from "./concepto/concepto.component";
import { Impuesto } from "../../models/cfdi/impuesto";
import { Traslado } from "../../models/cfdi/traslado";
import { CfdiValidatorService } from "../../util-services/cfdi-validator.service";
import { updateCfdi, updateInvoice } from "../../core.actions";
import { AppState } from "../../../reducers";
import { select, Store } from "@ngrx/store";
import { Factura } from "../../models/factura";
import { invoice } from "../../core.selectors";
import { InvoicesData } from "../../data/invoices-data";
import { NotificationsService } from "../../util-services/notifications.service";

@Component({
  selector: "nt-conceptos",
  templateUrl: "./conceptos.component.html",
  styleUrls: ["./conceptos.component.scss"],
})
export class ConceptosComponent implements OnInit {
  @Input() cfdi: Cfdi;
  @Input() allowEdit: Boolean;
  
  private factura: Factura;
  public loading: boolean;

  constructor(
    private store: Store<AppState>,
    private cfdiService: CfdiData,
    private cfdiValidator: CfdiValidatorService,
    private invoiceService: InvoicesData,
    private notificationService: NotificationsService,
    private dialogService: NbDialogService,
  ) {}

  ngOnInit() {
    this.loading = false;
    this.store.pipe(select(invoice)).subscribe((fact) => (this.factura = fact));
  }

  public async removeConcepto(index: number) {
    this.loading = true;
    try {
      const cfdi = JSON.parse(JSON.stringify(this.cfdi));
      cfdi.conceptos.splice(index, 1);

      const errors = this.cfdiValidator.validarCfdi(cfdi);
      if (errors.length > 0) {
        errors.forEach((e) => this.notificationService.sendNotification('warning',e,'Error validacion'));
      } else {
        if (this.cfdi.folio && this.cfdi.folio.length > 0) {
          // update invoice in backend side
          const fact = {...this.factura,cfdi:cfdi,total:cfdi.total}
          const invoice = await this.invoiceService.updateInvoice(fact).toPromise();
          this.store.dispatch(updateInvoice({ invoice }));
        } else {
          const result = await this.cfdiService
            .recalculateCfdi(cfdi)
            .toPromise();
          this.store.dispatch(updateCfdi({ cfdi: result }));
        }
      }
      this.loading = false;
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message,'Error removiendo concepto');
      this.loading = false;
    }
  }

  public async upsertConcepto(index?: number) {
    const c =
      index != undefined
        ? JSON.parse(JSON.stringify(this.cfdi.conceptos[index]))
        : new Concepto();
    this.loading = true;
    try {
      const submmited = await this.dialogService
        .open(ConceptoComponent, {
          context: {
            concepto: c,
          },
        })
        .onClose.toPromise();
      if (submmited) {
        const cfdi = JSON.parse(JSON.stringify(this.cfdi));
        if (index != undefined) {
          cfdi.conceptos[index] = submmited;
        } else {
          cfdi.conceptos.push(submmited);
        }
        const errors = this.cfdiValidator.validarCfdi(cfdi);
        if (errors.length > 0) {
          errors.forEach((e) => this.notificationService.sendNotification('warning',e,'Error validacion'));
        } else {
          if (this.cfdi.folio && this.cfdi.folio.length > 0) {
            // update invoice in backend side
            const result = await this.cfdiService
            .recalculateCfdi(cfdi)
            .toPromise();
          const fact = {...this.factura,cfdi:result,total:result.total}
          const invoice = await this.invoiceService.updateInvoice(fact).toPromise();
          this.store.dispatch(updateInvoice({ invoice }));
          } else {
            const result = await this.cfdiService
              .recalculateCfdi(cfdi)
              .toPromise();
            this.store.dispatch(updateCfdi({ cfdi: result }));
          }
        }
      }
      this.loading = false;
    } catch (error) {
      this.notificationService.sendNotification('danger',error?.message,'Error');
      this.loading = false;
    }
  }

  public calcularImpuestosTrasladados(impuestos: Impuesto[]): number {
    if (impuestos && impuestos.length > 0) {
      const traslados: Traslado[] = [];

      impuestos.forEach((i) => traslados.push(...i.traslados));
      return traslados
        .map((t) => t.importe)
        .reduce((total, value) => total + value);
    } else {
      return 0;
    }
  }
}
