import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdministracionComponent } from './administracion.component';
import { UsersComponent } from './users/users.component';
import { ClientesComponent } from '../commons/clientes/clientes.component';
import { ClienteComponent } from '../commons/cliente/cliente.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { UserComponent } from './user/user.component';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';
import { CuentasBancariasComponent } from '../commons/cuentas-bancarias/cuentas-bancarias.component';

const routes: Routes = [{
  path: '',
  component: AdministracionComponent,
  children: [
    {
      path: 'usuarios',
      component: UsersComponent,
    }, {
      path: 'usuarios/:id',
      component: UserComponent,
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
      path: 'empresas',
      component: EmpresasComponent,
    }, {
      path: 'empresa/:rfc',
      component: EmpresaComponent,
    },{
      path: 'cuentas-bancarias',
      component: CuentasBancariasComponent,
    },{
      path: 'cuenta-bancaria/:empresa/:cuenta',
      component: CuentaBancariaComponent,
    },{
      path: 'reportes',
      component: InvoiceReportsComponent,
    }]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdministracionRoutingModule { }
