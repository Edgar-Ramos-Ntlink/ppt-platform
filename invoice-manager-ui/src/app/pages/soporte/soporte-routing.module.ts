import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClienteComponent } from '../commons/cliente/cliente.component';
import { ClientesComponent } from '../commons/clientes/clientes.component';
import { CuentaBancariaComponent } from '../commons/cuenta-bancaria/cuenta-bancaria.component';
import { CuentasBancariasComponent } from '../commons/cuentas-bancarias/cuentas-bancarias.component';
import { EmpresaComponent } from '../commons/empresa/empresa.component';
import { EmpresasComponent } from '../commons/empresas/empresas.component';
import { InvoiceReportsComponent } from '../commons/invoice-reports/invoice-reports.component';
import { ReporteSoporteComponent } from '../commons/reporte-soporte/reporte-soporte.component';
import { SupportRequestComponent } from '../commons/support-request/support-request.component';
import { UserComponent } from '../commons/user/user.component';
import { UsersComponent } from '../commons/users/users.component';
import { SoporteComponent } from './soporte.component';

const routes: Routes = [
    {
        path: '',
        component: SoporteComponent,
        children: [
            {
                path: 'usuarios',
                component: UsersComponent,
            },
            {
                path: 'usuarios/:id',
                component: UserComponent,
            },
            {
                path: 'clientes',
                component: ClientesComponent,
            },
            {
                path: 'cliente/:id',
                component: ClienteComponent,
            },
            {
                path: 'empresas',
                component: EmpresasComponent,
            },
            {
                path: 'empresa/:rfc',
                component: EmpresaComponent,
            },
            {
                path: 'cuentas-bancarias',
                component: CuentasBancariasComponent,
            },
            {
                path: 'cuenta-bancaria/:empresa/:cuenta',
                component: CuentaBancariaComponent,
            },
            {
                path: 'reportes',
                component: InvoiceReportsComponent,
            },
            {
                path: 'reporte-soporte',
                component: ReporteSoporteComponent,
            },
            {
                path: 'soporte/:folio',
                component: SupportRequestComponent,
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class SoporteRoutingModule {}
