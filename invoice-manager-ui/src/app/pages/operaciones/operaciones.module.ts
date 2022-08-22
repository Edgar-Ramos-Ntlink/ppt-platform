import { NgModule } from '@angular/core';
import { OperacionesRoutingModule } from './operaciones-routing.module';
import { OperacionesComponent } from './operaciones.component';
import { RevisionComponent } from './revision/revision.component';
import { CommonsModule } from '../commons/commons.module';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { LineaXComponent } from './linea-x/linea-x.component';
import { AsignacionPagosComponent } from '../commons/asignacion-pagos/asignacion-pagos.component';
import { ValidacionPagoComponent } from '../commons/pagos/validacion-pago/validacion-pago.component';
import { CoreModule } from '../../@core/core.module';
import { SeleccionPagosComponent } from '../commons/devoluciones/seleccion-pagos/seleccion-pagos.component';
import { DetalleDevolucionComponent } from '../commons/devoluciones/detalle-devolucion/detalle-devolucion.component';


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
  entryComponents: [AsignacionPagosComponent, ValidacionPagoComponent, SeleccionPagosComponent, DetalleDevolucionComponent],
  providers: [ DonwloadFileService ],
})
export class OperacionesModule { }
