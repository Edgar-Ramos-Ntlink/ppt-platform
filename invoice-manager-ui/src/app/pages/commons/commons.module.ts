import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { GenerarComplementoComponent } from './generar-complemento/generar-complemento.component';
import { MulticomplementosComponent } from './multicomplementos/multicomplementos.component';
import { CuentasBancariasComponent } from './cuentas-bancarias/cuentas-bancarias.component';
import { CuentaBancariaComponent } from './cuenta-bancaria/cuenta-bancaria.component';

@NgModule({
    declarations: [
        ClientesComponent,
        ClienteComponent,
        EmpresaComponent,
        EmpresasComponent,
        PagosComponent,
        PagosFacturaComponent,
        ValidacionPagoComponent,
        InvoiceReportsComponent,
        AsignacionPagosComponent,
        GenerarComplementoComponent,
        MulticomplementosComponent,
        CuentasBancariasComponent,
        CuentasBancariasComponent,
        CuentaBancariaComponent,
    ],
    imports: [
        CommonModule,
        AutocompleteLibModule,
        FormsModule,
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
        InvoiceReportsComponent,
        PagosComponent,
        PagosFacturaComponent,
        ValidacionPagoComponent,
        GenerarComplementoComponent,
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
