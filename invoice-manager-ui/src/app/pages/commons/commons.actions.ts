import { createAction, props } from "@ngrx/store";
import { Client } from "../../models/client";
import { Devolucion } from "../../models/devolucion";

export const updateReturn = createAction(
    '[RETURNS] update return',
    props<{ return : Devolucion }>()
);

export const addClientReturn = createAction(
    '[RETURNS] adding client to return',
    props<{client:Client}>()
);