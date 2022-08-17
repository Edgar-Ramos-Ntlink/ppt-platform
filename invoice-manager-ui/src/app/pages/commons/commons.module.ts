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
import { PagosFacturaComponent } from './pagos-facturas/pagos-facturas.component';
import { ValidacionPagoComponent } from './pagos/validacion-pago/validacion-pago.component';
import { InvoiceReportsComponent } from './invoice-reports/invoice-reports.component';
import { AsignacionPagosComponent } from './asignacion-pagos/asignacion-pagos.component';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { MulticomplementosComponent } from './multicomplementos/multicomplementos.component';
import { CuentasBancariasComponent } from './cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from './cuenta-bancaria/cuenta-bancaria.component';
import { CoreModule } from '../../@core/core.module';
import { UsersComponent } from './users/users.component';
import { UserComponent } from './user/user.component';
import { DevolucionesComponent } from './devoluciones/devoluciones.component';
import { SeleccionPagosComponent } from './devoluciones/seleccion-pagos/seleccion-pagos.component';

@NgModule({
    declarations: [
        ClientesComponent,
        ClienteComponent,
        EmpresaComponent,
        EmpresasComponent,
        UsersComponent,
        UserComponent,
        PagosComponent,
        PagosFacturaComponent,
        ValidacionPagoComponent,
        InvoiceReportsComponent,
        AsignacionPagosComponent,
        MulticomplementosComponent,
        CuentasBancariasComponent,
        CuentasBancariasComponent,
        CuentaBancariaComponent,
        DevolucionesComponent,
        SeleccionPagosComponent,
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
        PagosFacturaComponent,
        ValidacionPagoComponent,
        MulticomplementosComponent,
        AsignacionPagosComponent,
        CuentasBancariasComponent,
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
