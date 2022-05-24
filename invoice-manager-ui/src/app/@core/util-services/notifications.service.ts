import { Injectable } from '@angular/core';
import { NbComponentStatus, NbGlobalPhysicalPosition, NbToastrService } from '@nebular/theme';
import { Store } from '@ngrx/store';
import { addNotification } from '../../@theme/theme.actions';
import { AppState } from '../../reducers';
import { AppNotification } from '../models/app-notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  constructor(private toastrService: NbToastrService,
    private store: Store<AppState>) { }


  public sendNotification(type:NbComponentStatus = 'info',message:string, title:string=''){
    this.showToast(type, title, message)
    this.store.dispatch(addNotification({notification:new AppNotification(type,message,title)}));
  }


  private showToast(
    type: NbComponentStatus,
    title: string,
    body: string,
    clickdestroy: boolean = true
  ) {
    const config = {
      status: type,
      destroyByClick: clickdestroy,
      duration: 8000,
      hasIcon: true,
      position: NbGlobalPhysicalPosition.TOP_RIGHT,
      preventDuplicates: true,
    };
    const titleContent = title ? `${title}` : "";

    this.toastrService.show(body, titleContent, config);
  }
}
