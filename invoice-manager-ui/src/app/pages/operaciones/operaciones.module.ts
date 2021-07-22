import { NgModule } from '@angular/core';
import { OperacionesRoutingModule } from './operaciones-routing.module';
import { OperacionesComponent } from './operaciones.component';
import { RevisionComponent } from './revision/revision.component';
import { CommonsModule } from '../commons/commons.module';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { ValidacionDevolucionComponent } from './devoluciones/validacion-devolucion/validacion-devolucion.component';
import { DownloadCsvService } from '../../@core/util-services/download-csv.service';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { LineaXComponent } from './linea-x/linea-x.component';
import { AsignacionPagosComponent } from '../commons/asignacion-pagos/asignacion-pagos.component';
import { ValidacionPagoComponent } from '../commons/pagos/validacion-pago/validacion-pago.component';
import { ObservacionPendientesComponent } from '../commons/observacion-pendientes/observacion-pendientes.component';


@NgModule({
  declarations: [
    OperacionesComponent,
    RevisionComponent,
    DevolucionesComponent,
    ValidacionDevolucionComponent,
    LineaXComponent],
  imports: [
    OperacionesRoutingModule,
    CommonsModule,
  ],
  entryComponents: [ValidacionDevolucionComponent,AsignacionPagosComponent, ObservacionPendientesComponent, ValidacionPagoComponent],
  providers: [ DownloadCsvService , DonwloadFileService ],
})
export class OperacionesModule { }
