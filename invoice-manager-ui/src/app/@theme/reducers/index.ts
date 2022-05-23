import { createReducer, on } from "@ngrx/store";
import { AppNotification } from "../../@core/models/app-notification";
import { ThemeActions } from "../theme-actions.type";

export const THEME_FEATURE_KEY = 'theme';

export const initialStateTheme : ThemeState = {
  notifications : []
}

export interface ThemeState {
  notifications : AppNotification[],
}

export const themeReducer = createReducer(
  initialStateTheme,
  on(ThemeActions.cleanInvoice,(state, action) => {
    return {
      notifications : []
    };
  }),

  on(ThemeActions.addNotification,(state,action)=>{
    let notifications:AppNotification[] = JSON.parse(JSON.stringify(state.notifications));
    notifications.unshift(action.notification)
    if(notifications.length>10){
      notifications = notifications.slice(0,10);
    }
    return {notifications:notifications}
  })
  );

