import { createAction, props } from '@ngrx/store';
import { Cfdi } from './models/cfdi/cfdi';
import { Emisor } from './models/cfdi/emisor';
import { Receptor } from './models/cfdi/receptor';
import { Factura } from './models/factura';

export const initInvoice = createAction(
    '[INVOICE - initialize] initialize invoice',
    props<{ invoice: Factura }>()
);

export const updateInvoice = createAction(
    '[INVOICE - update] update invoice',
    props<{ invoice: Factura }>()
);

export const cleanInvoice = createAction('[INVOICE - clean] clean invoice');

export const updateReceptor = createAction(
    '[CFDI - receptor] update receptor',
    props<{ receptor: Receptor }>()
);

export const updateEmisor = createAction(
    '[CFDI - emisor] update emisor',
    props<{ emisor: Emisor }>()
);

export const updateReceptorAddress = createAction(
    '[INVOICE - receptorAddress] updating receptor address',
    props<{ address: string }>()
);

export const updateEmisorAddress = createAction(
    '[INVOICE - emisorAddress] updating emisor address',
    props<{ address: string }>()
);

export const updateCfdi = createAction(
    '[CFDI - update] update cfdi',
    props<{ cfdi: Cfdi }>()
);
