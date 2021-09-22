import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { LegalComponent } from './legal.component';

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
    }
  ]}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LegalRoutingModule { }