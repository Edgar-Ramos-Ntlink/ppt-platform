import { NgModule } from '@angular/core';
import { NbDialogModule } from '@nebular/theme';

import { ContabilidadRoutingModule } from './contabilidad-routing.module';
import { ContabilidadComponent } from './contabilidad.component';
import { ClientesComponent } from './clientes/clientes.component';
import { ClienteComponent } from './cliente/cliente.component';
import { EmpresasComponent } from './empresas/empresas.component';
import { EmpresaComponent } from './empresa/empresa.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';
import { ReportesComponent } from './reportes/reportes.component';
import { InvoiceGeneratorComponent } from './invoice-generator/invoice-generator.component';
import { InvoiceRequestComponent } from './invoice-generator/invoice-request/invoice-request.component';
import { TransferenciasComponent } from './transferencias/transferencias.component';
import { CommonsModule } from '../commons/commons.module';

@NgModule({
  declarations: [ContabilidadComponent,
    PreCfdiComponent,
    ReportesComponent,
    InvoiceGeneratorComponent,
    InvoiceRequestComponent,
    TransferenciasComponent],
  imports: [
    ContabilidadRoutingModule,
    CommonsModule,
    NbDialogModule.forChild(),
  ],
  entryComponents:[InvoiceRequestComponent]
})
export class ContabilidadModule { }
