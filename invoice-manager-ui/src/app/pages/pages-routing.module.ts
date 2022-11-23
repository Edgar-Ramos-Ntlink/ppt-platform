import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SupportRequestComponent } from './commons/support-request/support-request.component';

const routes: Routes = [
    {
        path: '',
        component: PagesComponent,
        children: [
            {
                path: 'dashboard',
                component: DashboardComponent,
            },
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full',
            },
            {
                path: 'soporte/:folio',
                pathMatch: 'full',
                component: SupportRequestComponent,
            },
            {
                path: 'promotor',
                loadChildren: () =>
                    import('./promotor/promotor.module').then(
                        (m) => m.PromotorModule
                    ),
            },
            {
                path: 'operaciones',
                loadChildren: () =>
                    import('./operaciones/operaciones.module').then(
                        (m) => m.OperacionesModule
                    ),
            },
            {
                path: 'legal',
                loadChildren: () =>
                    import('./legal/legal.module').then((m) => m.LegalModule),
            },
            {
                path: 'contabilidad',
                loadChildren: () =>
                    import('./contabilidad/contabilidad.module').then(
                        (m) => m.ContabilidadModule
                    ),
            },
            {
                path: 'tesoreria',
                loadChildren: () =>
                    import('./tesoreria/tesoreria.module').then(
                        (m) => m.TesoreriaModule
                    ),
            },
            {
                path: 'bancos',
                loadChildren: () =>
                    import('./bancos/bancos.module').then(
                        (m) => m.BancosModule
                    ),
            },
            {
                path: 'administracion',
                loadChildren: () =>
                    import('./administracion/administracion.module').then(
                        (m) => m.AdministracionModule
                    ),
            },
            {
                path: '**',
                component: DashboardComponent,
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class PagesRoutingModule {}
