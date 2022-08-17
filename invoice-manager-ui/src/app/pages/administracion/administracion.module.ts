import { NgModule } from '@angular/core';
import { AdministracionRoutingModule } from './administracion-routing.module';
import { AdministracionComponent } from './administracion.component';
import { CommonsModule } from '../commons/commons.module';

@NgModule({
  declarations: [
    AdministracionComponent],
   
  imports: [
    AdministracionRoutingModule,
    CommonsModule
  ],
  providers: [ ],
})
export class AdministracionModule { }
