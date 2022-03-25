import { NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PromotorComponent} from './promotor.component';
import { ClientesComponent } from '../commons/clientes/clientes.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { ClienteComponent } from '../commons/cliente/cliente.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { PagosFacturaComponent } from '../commons/pagos-factura/pagos-factura.component';

const routes: Routes = [{
  path: '',
  component: PromotorComponent,
  children: [
    {
      path: 'precfdi/:folio',
      component: PreCfdiComponent,
    }, {
      path: 'clientes',
      component: ClientesComponent,
    }, {
      path: 'cliente/:rfc',
      component: ClienteComponent,
    }, {
      path: 'cliente/:rfc/:promotor', 
      component: ClienteComponent,
    }, {
      path: 'reportes',
      component: InvoiceReportsComponent,
    }, {
      path: 'pago-facturas',
      component: PagosFacturaComponent,
    },
  ]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PromotorRoutingModule { }
