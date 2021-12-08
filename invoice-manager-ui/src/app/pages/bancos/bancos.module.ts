import { NgModule } from '@angular/core';
import { BancosComponent } from './bancos.component';
import { DownloadCsvService } from '../../@core/util-services/download-csv.service';
import { CommonsModule } from '../commons/commons.module';
import { BancosRoutingModule } from './bancos-routing.module';
import { DonwloadFileService } from '../../@core/util-services/download-file-service';

@NgModule({
  declarations: [
    BancosComponent,
  ],
  imports: [
    BancosRoutingModule,
    CommonsModule,
  ],
  entryComponents: [],
  providers: [ DownloadCsvService , DonwloadFileService ],
})
export class BancosModule { }
