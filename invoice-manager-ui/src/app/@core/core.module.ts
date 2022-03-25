import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LayoutService } from './utils';
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
  NbStepperModule,
  NbDialogModule,
  NbIconModule,
  NbTreeGridModule,
  NbMenuModule,
  NbUserModule,
  NbSpinnerModule,
} from '@nebular/theme';



import { UsersData } from './data/users-data';
import { UsersService } from './back-services/users.service';


import { CatalogsData } from './data/catalogs-data';
import { CompaniesData } from './data/companies-data';
import { InvoicesData } from './data/invoices-data';
import { CfdiData } from './data/cfdi-data';
import { ClientsData } from './data/clients-data';
import { PaymentsData } from './data/payments-data';

import { CatalogsService } from './back-services/catalogs.service';
import { ClientsService } from './back-services/clients.service';
import { CompaniesService } from './back-services/companies.service';
import { InvoicesService } from './back-services/invoices.service';
import { CfdiService } from './back-services/cfdi.service';
import { PaymentsService } from './back-services/payments.service';
import { FilesData } from './data/files-data';
import { FilesService } from './back-services/files.service';
import { TransferData } from './data/transfers-data';
import { TransferService } from './back-services/transfer.service';
import { CuentasData } from './data/cuentas-data';
import { CuentasService } from './back-services/cuentas.service';

const DATA_SERVICES = [
  {provide: CatalogsData, useClass: CatalogsService},
  {provide: ClientsData, useClass: ClientsService},
  {provide: CompaniesData, useClass: CompaniesService},
  {provide: CuentasData, useClass: CuentasService},
  {provide: InvoicesData, useClass: InvoicesService},
  {provide: CfdiData, useClass: CfdiService},
  {provide: PaymentsData, useClass : PaymentsService},
  {provide: TransferData , useClass: TransferService},
  {provide: FilesData, useClass : FilesService},
  {provide: UsersData, useClass: UsersService }
];


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NbActionsModule,
    NbButtonModule,
    NbCardModule,
    NbTabsetModule,
    NbCheckboxModule,
    NbDatepickerModule,
    NbInputModule,
    NbRadioModule,
    NbSelectModule,
    NbStepperModule,
    NbDialogModule,
    NbIconModule,
    NbTreeGridModule,
    NbMenuModule,
    NbUserModule,
    NbSpinnerModule,
  ],
  declarations: [],
  exports:[
    NbActionsModule,
    NbButtonModule,
    NbCardModule,
    NbTabsetModule,
    NbCheckboxModule,
    NbDatepickerModule,
    NbInputModule,
    NbRadioModule,
    NbSelectModule,
    NbStepperModule,
    NbDialogModule,
    NbIconModule,
    NbTreeGridModule,
    NbMenuModule,
    NbUserModule,
    NbSpinnerModule,
  ]
})
export class CoreModule {
  
  static forRoot(): ModuleWithProviders<CoreModule> {
    return {
      ngModule: CoreModule,
      providers: [
        LayoutService,
        ...DATA_SERVICES
      ],
    };
  }
}
