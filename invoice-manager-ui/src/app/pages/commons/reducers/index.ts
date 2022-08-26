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
  }));
