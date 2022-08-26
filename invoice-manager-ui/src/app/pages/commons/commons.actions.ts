import { createAction, props } from "@ngrx/store";
import { Devolucion } from "../../models/devolucion";

export const updateReturn = createAction(
    '[RETURNS] update return',
    props<{ return : Devolucion }>()
);