import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TesoreriaComponent } from './tesoreria.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { PagosComponent } from '../commons/pagos/pagos.component';
import { DevolutionsDetailsComponent } from '../commons/devolutions-details/devolutions-details.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { CuentasBancariasComponent } from '../commons/cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';



const routes: Routes = [{
  path: '',
  component: TesoreriaComponent,
  children: [
    {
      path: 'validacion-pagos',
      component: PagosComponent,
    },
    {
      path: 'historial-pagos',
      component: PagosComponent,
    },
    {
      path: 'devoluciones',
      component: DevolucionesComponent,
    },
    {
      path: 'reportes',
      component : InvoiceReportsComponent,
    },
    {
      path: 'facturas/:folio/devoluciones',
      component: DevolutionsDetailsComponent,
    },
    {
      path: 'cuentas-bancarias',
      component: CuentasBancariasComponent,
    },
    {
      path: 'cuenta-bancaria/:empresa/:cuenta',
      component: CuentaBancariaComponent,
    }, {
      path: 'empresas',
      component: EmpresasComponent,
    }, {
      path: 'empresa/:rfc',
      component: EmpresaComponent,
    },
  ]
}]; 

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TesoreriaRoutingModule { }
