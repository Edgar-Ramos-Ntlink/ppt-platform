import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SoporteRoutingModule } from './soporte-routing.module';
import { SoporteComponent } from './soporte.component';

@NgModule({
    declarations: [SoporteComponent],
    imports: [CommonModule, SoporteRoutingModule],
})
export class SoporteModule {}
