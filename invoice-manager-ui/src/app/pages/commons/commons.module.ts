import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
    NbActionsModule,
    NbButtonModule,
    NbCardModule,
    NbTabsetModule,
    NbCheckboxModule,
    NbDatepickerModule,
    NbInputModule,
    NbRadioModule,
    NbSelectModule,
    NbUserModule,
    NbStepperModule,
    NbDialogModule,
    NbIconModule,
    NbTreeGridModule,
    NbSpinnerModule,
} from '@nebular/theme';

import { ClientesComponent } from './clientes/clientes.component';
import { ClienteComponent } from './cliente/cliente.component';
import { EmpresaComponent } from './empresa/empresa.component';
import { EmpresasComponent } from './empresas/empresas.component';
import { PagosComponent } from './pagos/pagos.component';
import { ValidacionPagoComponent } from './pagos/validacion-pago/validacion-pago.component';
import { InvoiceReportsComponent } from './invoice-reports/invoice-reports.component';
import { MulticomplementosComponent } from './multicomplementos/multicomplementos.component';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { CuentasBancariasComponent } from './cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from './cuenta-bancaria/cuenta-bancaria.component';
import { CoreModule } from '../../@core/core.module';
import { UsersComponent } from './users/users.component';
import { UserComponent } from './user/user.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { SeleccionPagosComponent } from './devoluciones/seleccion-pagos/seleccion-pagos.component';
import { DetalleDevolucionComponent } from './devoluciones/detalle-devolucion/detalle-devolucion.component';
import { StoreModule } from '@ngrx/store';
import * as fromCommons from './reducers';

@NgModule({
    declarations: [
        ClientesComponent,
        ClienteComponent,
        EmpresaComponent,
        EmpresasComponent,
        UsersComponent,
        UserComponent,
        PagosComponent,
        ValidacionPagoComponent,
        InvoiceReportsComponent,
        MulticomplementosComponent,
        CuentasBancariasComponent,
        CuentasBancariasComponent,
        CuentaBancariaComponent,
        DevolucionesComponent,
        SeleccionPagosComponent,
        DetalleDevolucionComponent,
    ],
    imports: [
        CommonModule,
        CoreModule,
        AutocompleteLibModule,
        FormsModule,
        ReactiveFormsModule,
        NbTabsetModule,
        NbActionsModule,
        NbButtonModule,
        NbCardModule,
        NbCheckboxModule,
        NbDatepickerModule,
        NbInputModule,
        NbRadioModule,
        NbSelectModule,
        NbUserModule,
        NbStepperModule,
        NbDialogModule.forChild(),
        NbIconModule,
        NbSpinnerModule,
        NbTreeGridModule,
        StoreModule.forFeature(fromCommons.COMMONS_FEATURE_KEY, fromCommons.commonsReducer),
    ],
    exports: [
        ClientesComponent,
        ClienteComponent,
        EmpresaComponent,
        EmpresasComponent,
        UsersComponent,
        UserComponent,
        InvoiceReportsComponent,
        PagosComponent,
        ValidacionPagoComponent,
        MulticomplementosComponent,
        CuentasBancariasComponent,
        DevolucionesComponent,
        SeleccionPagosComponent,
        DetalleDevolucionComponent,
        AutocompleteLibModule,
        FormsModule,
        CommonModule,
        NbTabsetModule,
        NbActionsModule,
        NbButtonModule,
        NbCardModule,
        NbCheckboxModule,
        NbDatepickerModule,
        NbInputModule,
        NbRadioModule,
        NbSelectModule,
        NbUserModule,
        NbStepperModule,
        NbDialogModule,
        NbIconModule,
        NbSpinnerModule,
        NbTreeGridModule,
    ],
})
export class CommonsModule {}
