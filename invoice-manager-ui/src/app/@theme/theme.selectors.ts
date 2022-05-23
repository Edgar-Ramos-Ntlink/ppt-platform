import { createFeatureSelector, createSelector } from "@ngrx/store";
import { ThemeState } from "./reducers";

export const selectThemeState = createFeatureSelector<ThemeState>("theme");


export const notifications = createSelector(selectThemeState, (state) => state.notifications);