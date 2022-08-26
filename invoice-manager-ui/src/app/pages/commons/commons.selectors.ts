import { createFeatureSelector, createSelector } from "@ngrx/store";
import { CommonsState } from "./reducers";

export const selectCommonsState = createFeatureSelector<CommonsState>("commons");

export const returnSelector = createSelector(
  selectCommonsState,
  (commons) => commons.return
);

export const returnDetailsSelector = createSelector(
    selectCommonsState,
    (commons)=> commons?.return.detalles
)
