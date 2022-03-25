import { NgModule } from '@angular/core';
import { TesoreriaRoutingModule } from './tesoreria-routing.module';
import { TesoreriaComponent } from './tesoreria.component';
import { ValidacionPagoComponent } from '../commons/pagos/validacion-pago/validacion-pago.component';

import { IngresosComponent } from './ingresos/ingresos.component';
import { EgresosComponent } from './egresos/egresos.component';
import { ConciliacionComponent } from './conciliacion/conciliacion.component';
import { CommonsModule } from '../commons/commons.module';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';


@NgModule({
  declarations: [
    TesoreriaComponent,
    IngresosComponent,
    EgresosComponent,
    ConciliacionComponent,
  ],
  imports: [
    TesoreriaRoutingModule,
    CommonsModule,
  ],
  entryComponents: [
    ValidacionPagoComponent,],
  providers: [ DonwloadFileService ],
})
export class TesoreriaModule { }
