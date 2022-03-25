import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TesoreriaComponent } from './tesoreria.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { PagosComponent } from '../commons/pagos/pagos.component';



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
      path: 'reportes',
      component : InvoiceReportsComponent,
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
