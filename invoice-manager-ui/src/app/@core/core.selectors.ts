import { createFeatureSelector, createSelector } from "@ngrx/store";
import { compare } from "mathjs";
import { CoreState } from "./reducers";

export const selectCoreState = createFeatureSelector<CoreState>("core");

export const emisorDireccion = createSelector(
  selectCoreState,
  (core) => core.invoice?.direccionEmisor
);

export const invoice = createSelector(selectCoreState, (core) => core.invoice);

export const cfdi = createSelector(
  selectCoreState,
  (core) => core.invoice?.cfdi
);

export const complementos = createSelector(selectCoreState,(core) => core.complementos);

export const emisor = createSelector(
  selectCoreState,
  (core) => core.invoice?.cfdi.emisor
);

export const conceptos = createSelector(
  selectCoreState,
  (core) => core.invoice?.cfdi.conceptos
);

export const receptorDireccion = createSelector(
  selectCoreState,
  (core) => core.invoice?.direccionReceptor
);
