import { ModuleWithProviders, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
import { AutocompleteLibModule } from 'angular-ng-autocomplete';

import { CatalogsData } from './data/catalogs-data';
import { ClientsData } from './data/clients-data';
import { CompaniesData } from './data/companies-data';
import { CuentasData } from './data/cuentas-data';
import { InvoicesData } from './data/invoices-data';
import { CfdiData } from './data/cfdi-data';
import { PaymentsData } from './data/payments-data';
import { TransferData } from './data/transfers-data';
import { UsersData } from './data/users-data';
import { FilesData } from './data/files-data';
import { CatalogsService } from './back-services/catalogs.service';
import { ClientsService } from './back-services/clients.service';
import { CompaniesService } from './back-services/companies.service';
import { CuentasService } from './back-services/cuentas.service';
import { InvoicesService } from './back-services/invoices.service';
import { CfdiService } from './back-services/cfdi.service';
import { PaymentsService } from './back-services/payments.service';
import { TransferService } from './back-services/transfer.service';
import { FilesService } from './back-services/files.service';
import { UsersService } from './back-services/users.service';
import { CfdiComponent } from './components/cfdi/cfdi.component';
import { ConceptosComponent } from './components/conceptos/conceptos.component';
import { ReceptorComponent } from './components/receptor/receptor.component';
import { EmisorComponent } from './components/emisor/emisor.component';
import { ConceptoComponent } from './components/conceptos/concepto/concepto.component';
import { PagoFacturaComponent } from './components/pago-factura/pago-factura.component';
import { StoreModule } from '@ngrx/store';
import * as fromCore from './reducers';
import { CfdiValidatorService } from './util-services/cfdi-validator.service';
import { ClientsValidatorService } from './util-services/clients-validator.service';
import { CompaniesValidatorService } from './util-services/companies-validator.service';
import { UtilsService } from './util-services/utils.service';
import { DonwloadFileService } from './util-services/download-file-service';
import { PagosValidatorService } from './util-services/pagos-validator.service';
import { ComplementosPagoComponent } from './components/complementos-pago/complementos-pago.component';
import { InvoiceStatusComponent } from './components/invoice-status/invoice-status.component';
import { GenerarComplementoComponent } from '../pages/commons/generar-complemento/generar-complemento.component';
import { SupportData } from './data/support-data';
import { SupportService } from './back-services/support.service';

const DATA_SERVICES = [
    { provide: CatalogsData, useClass: CatalogsService },
    { provide: ClientsData, useClass: ClientsService },
    { provide: CompaniesData, useClass: CompaniesService },
    { provide: CuentasData, useClass: CuentasService },
    { provide: InvoicesData, useClass: InvoicesService },
    { provide: CfdiData, useClass: CfdiService },
    { provide: PaymentsData, useClass: PaymentsService },
    { provide: TransferData, useClass: TransferService },
    { provide: UsersData, useClass: UsersService },
    { provide: FilesData, useClass: FilesService },
    { provide: SupportData, useClass: SupportService },
];

const UTIL_SERVICES = [
    CfdiValidatorService,
    ClientsValidatorService,
    CompaniesValidatorService,
    DonwloadFileService,
    PagosValidatorService,
    UtilsService,
];

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
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
        AutocompleteLibModule,
        StoreModule.forFeature(fromCore.CORE_FEATURE_KEY, fromCore.coreReducer),
    ],
    declarations: [
        ReceptorComponent,
        CfdiComponent,
        ConceptosComponent,
        EmisorComponent,
        ConceptoComponent,
        PagoFacturaComponent,
        GenerarComplementoComponent,
        ComplementosPagoComponent,
        InvoiceStatusComponent,
    ],
    exports: [
        CommonModule,
        FormsModule,
        AutocompleteLibModule,
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
        ReceptorComponent,
        EmisorComponent,
        CfdiComponent,
        ConceptosComponent,
        ConceptoComponent,
        PagoFacturaComponent,
        InvoiceStatusComponent,
    ],
})
export class CoreModule {
    static forRoot(): ModuleWithProviders<CoreModule> {
        return {
            ngModule: CoreModule,
            providers: [LayoutService, ...DATA_SERVICES, ...UTIL_SERVICES],
        };
    }
}
