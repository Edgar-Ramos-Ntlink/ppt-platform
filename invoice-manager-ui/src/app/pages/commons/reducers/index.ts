import { state } from '@angular/animations';
import { createReducer, on
} from '@ngrx/store';
import { Devolucion } from '../../../models/devolucion';
import { CommonsActions } from '../common-actions.types';

export const COMMONS_FEATURE_KEY = 'commons';

export const initialCommonsState: CommonsState = {
  return: undefined
};

export interface CommonsState {
  return: Devolucion
}

export const commonsReducer = createReducer(
  initialCommonsState,
  on(CommonsActions.updateReturn,(state,action)=>{
    return {
      return : action.return
    }
  }),
  on(CommonsActions.addClientReturn,(state,action) =>{
    
    const dev = {...state.return};
    dev.clientes.push(action.client);
    return {
      return :dev
    }
  })
  );
