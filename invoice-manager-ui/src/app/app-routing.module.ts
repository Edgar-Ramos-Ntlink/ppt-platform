import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { UnauthorizedComponent } from './auth/unauthorized/unauthorized.component';
import { UnavailableServiceComponent } from './auth/unavailable-service/unavailable-service.component';
import { SesionLostComponent } from './auth/sesion-lost/sesion-lost.component';
import { AuthGuard } from './auth/auth.guard';

const routes: Routes = [
    {
        path: 'pages',
        loadChildren: () =>
            import('./pages/pages.module').then((m) => m.PagesModule),
        canActivate: [AuthGuard],
    },
    {
        path: 'unauthorized',
        component: UnauthorizedComponent,
    },
    {
        path: 'unavailable',
        component: UnavailableServiceComponent,
    },
    {
        path: 'sesion-lost',
        component: SesionLostComponent,
    },

    { path: '', redirectTo: 'pages', pathMatch: 'full' },
    { path: '**', redirectTo: 'pages' },
];

const config: ExtraOptions = {
    useHash: true,
};

@NgModule({
    imports: [RouterModule.forRoot(routes, config)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
