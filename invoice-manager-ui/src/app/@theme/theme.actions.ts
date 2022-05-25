import { createAction, props } from "@ngrx/store";
import { AppNotification } from "../@core/models/app-notification";


export const cleanInvoice = createAction("[THEME - clean notifications] clean notifications");

export const updateNotifications = createAction(
    "[THEME - update notifications] update notifications",props<{ notifications: AppNotification[] }>());

export const addNotification = createAction(
        "[THEME - add notification] add notification",props<{ notification: AppNotification }>())