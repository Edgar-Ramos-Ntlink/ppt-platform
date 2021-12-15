import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { LegalComponent } from './legal.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';

const routes: Routes = [{
  path: '',
  component: LegalComponent,
  children: [
    {
      path: 'empresas',
      component: EmpresasComponent,
    }, {
      path: 'empresa/:rfc',
      component: EmpresaComponent,
    },
    {
      path: 'cuenta-bancaria/:empresa/:cuenta',
      component: CuentaBancariaComponent,
    },
  ]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LegalRoutingModule { }