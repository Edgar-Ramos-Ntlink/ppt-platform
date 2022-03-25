import { NgModule } from '@angular/core';


import { PromotorRoutingModule } from './promotor-routing.module';
import { PromotorComponent } from './promotor.component';
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component';

import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { CommonsModule } from '../commons/commons.module';
import { AsignacionPagosComponent } from '../commons/asignacion-pagos/asignacion-pagos.component';


@NgModule({
  declarations: [PromotorComponent,
    PreCfdiComponent,
  ],
  imports: [
    PromotorRoutingModule,
    CommonsModule,
  ],
  entryComponents: [
    AsignacionPagosComponent,
  ],
  providers: [ DonwloadFileService ],
})
export class PromotorModule { }
