import { createReducer, on } from "@ngrx/store";
import { CoreActions } from "../core-actions.types";
import { ComplementoPago } from "../models/complemento-pago";
import { Factura } from "../models/factura";

export const CORE_FEATURE_KEY = "core";

export const initialCoreState: CoreState = {
  invoice: undefined,
  complementos: undefined,
};

export interface CoreState {
  invoice: Factura,
  complementos: ComplementoPago[]
}

export const coreReducer = createReducer(
  initialCoreState,

  on(CoreActions.initInvoice, (state, action) => {
    return {
      invoice: action.invoice,
      complementos: []
    };
  }),

  on(CoreActions.updateInvoice, (state, action) => {
    return {
      invoice: action.invoice,
      complementos: state.complementos
    };
  }),

  on(CoreActions.cleanInvoice, (state, action) => {
    return {
      invoice: new Factura(),
      complementos: []
    };
  }),

  on(CoreActions.updateReceptorAddress, (state, action) => {
    return {
      invoice: { ...state.invoice, direccionReceptor: action.address },
      complementos: state.complementos
    };
  }),

  on(CoreActions.updateEmisorAddress, (state, action) => {
    return {
      invoice: { ...state.invoice, direccionEmisor: action.address },
      complementos: state.complementos
    };
  }),

  on(CoreActions.updateCfdi, (state, action) => {
    return {
      invoice: { ...state.invoice, cfdi: action.cfdi },
      complementos: state.complementos
    };
  }),

  on(CoreActions.updateEmisor, (state, action) => {
    const cfdi = { ...state.invoice.cfdi, emisor: action.emisor };
    return {
      invoice: {
        ...state.invoice,
        cfdi: cfdi,
      },
      complementos: state.complementos
    };
  }),

  on(CoreActions.updateReceptor, (state, action) => {
    const cfdi = { ...state.invoice.cfdi, receptor: action.receptor };
    return {
      invoice: {
        ...state.invoice,
        cfdi: cfdi,
      },
      complementos: state.complementos
    };
  })
);
