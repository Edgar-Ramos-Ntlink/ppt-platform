
import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { CuentasBancariasComponent } from '../commons/cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { BancosComponent } from './bancos.component';

const routes: Routes = [{
    path: '',
    component: BancosComponent,
    children: [
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
    ]}];

  @NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
  })
export class BancosRoutingModule { }
