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
import { SupportRequestComponent } from './support-request/support-request.component';
import { ReporteSoporteComponent } from './reporte-soporte/reporte-soporte.component';

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
        SupportRequestComponent,
        ReporteSoporteComponent,
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
        ValidacionPagoComponent,
        MulticomplementosComponent,
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
