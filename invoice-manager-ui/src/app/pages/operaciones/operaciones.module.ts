import { NgModule } from '@angular/core';
import { OperacionesRoutingModule } from './operaciones-routing.module';
import { OperacionesComponent } from './operaciones.component';
import { RevisionComponent } from './revision/revision.component';
import { CommonsModule } from '../commons/commons.module';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { LineaXComponent } from './linea-x/linea-x.component';
import { MulticomplementosComponent } from '../commons/multicomplementos/multicomplementos.component';
import { ValidacionPagoComponent } from '../commons/pagos/validacion-pago/validacion-pago.component';
import { CoreModule } from '../../@core/core.module';


@NgModule({
  declarations: [
    OperacionesComponent,
    RevisionComponent,
    LineaXComponent],
  imports: [
    OperacionesRoutingModule,
    CommonsModule,
    CoreModule,
  ],
  entryComponents: [MulticomplementosComponent, ValidacionPagoComponent],
  providers: [ DonwloadFileService ],
})
export class OperacionesModule { }
