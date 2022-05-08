import { createAction, props } from "@ngrx/store";
import { Cfdi } from "./models/cfdi/cfdi";
import { Emisor } from "./models/cfdi/emisor";
import { Receptor } from "./models/cfdi/receptor";
import { ComplementoPago } from "./models/complemento-pago";
import { Factura } from "./models/factura";

export const initInvoice = createAction(
  "[INVOICE - initialize] initialize invoice",
  props<{ invoice: Factura }>()
);

export const updateInvoice = createAction(
  "[INVOICE - update] update invoice",
  props<{ invoice: Factura }>()
);

export const cleanInvoice = createAction("[INVOICE - clean] clean invoice");

export const addReceptor = createAction(
  "[CFDI - receptor] adding receptor",
  props<{ receptor: Receptor }>()
);

export const addEmisor = createAction(
  "[CFDI - emisor] adding emisor",
  props<{ emisor: Emisor }>()
);

export const updateReceptorAddress = createAction(
  "[INVOICE - receptorAddress] updating receptor address",
  props<{ address: string }>()
);

export const updateEmisorAddress = createAction(
  "[INVOICE - emisorAddress] updating emisor address",
  props<{ address: string }>()
);

export const updateCfdi = createAction(
  "[CFDI - update] update cfdi",
  props<{ cfdi: Cfdi }>()
);

export const updateComplementosPago = createAction(
  "[CFDI - complementos] update complementos pago",
  props<{ complementos: ComplementoPago[] }>()
);
