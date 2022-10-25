import { NgModule} from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PromotorComponent} from './promotor.component';
import { ClientesComponent } from '../commons/clientes/clientes.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { ClienteComponent } from '../commons/cliente/cliente.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { MulticomplementosComponent } from '../commons/multicomplementos/multicomplementos.component';


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
      path: 'cliente/:id',
      component: ClienteComponent,
    }, {
      path: 'reportes',
      component: InvoiceReportsComponent,
    }, {
      path: 'pago-facturas',
      component: MulticomplementosComponent,
    }
  ]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PromotorRoutingModule { }
