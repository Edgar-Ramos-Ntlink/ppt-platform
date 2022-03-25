import { NgModule } from '@angular/core';
import { NbDialogModule } from '@nebular/theme';

import { CommonsModule } from '../commons/commons.module';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';
import { LegalComponent } from './legal.component';
import { LegalRoutingModule } from './legal-routing.module';

@NgModule({
  declarations: [LegalComponent],
  imports: [
    LegalRoutingModule,
    CommonsModule,
    NbDialogModule.forChild(),
  ],
  providers: [ DonwloadFileService ],
})
export class LegalModule { }
