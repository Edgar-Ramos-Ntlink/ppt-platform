import { ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NbAuthModule, NbDummyAuthStrategy } from '@nebular/auth';
import { NbSecurityModule, NbRoleProvider } from '@nebular/security';
import { of as observableOf } from 'rxjs';

import { throwIfAlreadyLoaded } from './module-import-guard';
import {
  LayoutService,
  PlayerService,
  StateService,
} from './utils';
import { MockDataModule } from './mock/mock-data.module';

import { UsersData } from './data/users-data';
import { CatalogsData } from './data/catalogs-data';


import { UsersService } from './back-services/users.service';
import { CatalogsService } from './back-services/catalogs.service';
import { ClientsData } from './data/clients-data';
import { ClientsService } from './back-services/clients.service';
import { CompaniesData } from './data/companies-data';
import { CompaniesService } from './back-services/companies.service';
import { InvoicesData } from './data/invoices-data';
import { InvoicesService } from './back-services/invoices.service';
import { DevolutionData } from './data/devolution-data';
import { DevolutionService } from './back-services/devolution.service';


const DATA_SERVICES = [
  {provide: CatalogsData, useClass: CatalogsService},
  {provide: ClientsData, useClass: ClientsService},
  {provide: CompaniesData, useClass: CompaniesService},
  {provide: InvoicesData, useClass: InvoicesService},
  {provide: DevolutionData, useClass: DevolutionService},
  {provide: UsersData, useClass: UsersService },
];

export class NbSimpleRoleProvider extends NbRoleProvider {
  getRole() {
    // here you could provide any role based on any auth flow
    return observableOf('guest');
  }
}

export const NB_CORE_PROVIDERS = [
  ...MockDataModule.forRoot().providers,
  ...DATA_SERVICES,
  ...NbAuthModule.forRoot({

    strategies: [
      NbDummyAuthStrategy.setup({
        name: 'email',
        delay: 3000,
      }),
    ],
    forms: {
    },
  }).providers,

  NbSecurityModule.forRoot({
    accessControl: {
      guest: {
        view: '*',
      },
      user: {
        parent: 'guest',
        create: '*',
        edit: '*',
        remove: '*',
      },
    },
  }).providers,

  {
    provide: NbRoleProvider, useClass: NbSimpleRoleProvider,
  },
  LayoutService,
  PlayerService,
  StateService,
];

@NgModule({
  imports: [
    CommonModule,
  ],
  exports: [
    NbAuthModule,
  ],
  declarations: [],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }

  static forRoot(): ModuleWithProviders {
    return <ModuleWithProviders>{
      ngModule: CoreModule,
      providers: [
        ...NB_CORE_PROVIDERS,
      ],
    };
  }
}
