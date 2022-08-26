import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { NbDialogRef, NbDialogService } from '@nebular/theme';
import { select, Store } from '@ngrx/store';
import { NotificationsService } from '../../../../@core/util-services/notifications.service';
import { ReturnsUtilsService } from '../../../../@core/util-services/returns-utils.service';
import { Devolucion, ReferenciaDevolucion, TipoDevolucion } from '../../../../models/devolucion';
import { AppState } from '../../../../reducers';
import { returnDetailsSelector, returnSelector } from '../../commons.selectors';

@Component({
  selector: 'nt-detalle-devolucion',
  templateUrl: './detalle-devolucion.component.html',
  styleUrls: ['./detalle-devolucion.component.scss']
})
export class DetalleDevolucionComponent implements OnInit {

  @Input() public type: TipoDevolucion;
  @Input() public amount : number;
  @Input() public allowEdit : boolean;

  public return : Devolucion = new Devolucion();
  public pagos: ReferenciaDevolucion[] = [];
  public pendingAmount:number = 0;
  
  constructor( private dialogService: NbDialogService,
    private returnUtils : ReturnsUtilsService,
    private notification : NotificationsService,
    private store: Store<AppState>) { }

  ngOnInit(): void {
    this.store.pipe(select(returnSelector))
    .subscribe(result => {
      this.return = result;
      this.amount = this.returnUtils.getAmountByType(this.type,result);
      this.pagos =[... result.detalles.filter(d=>this.type === d.receptorPago)];
      this.pendingAmount = this.amount - this.pagos.map(p=>p.monto).reduce((a,b)=>a+b,0);
    })
  }

  public addReturnPayment(dialog:TemplateRef<any>){
   
      this.dialogService
                    .open(dialog, { context: new ReferenciaDevolucion(this.type,+this.pendingAmount.toFixed(2)) })
                    .onClose.subscribe((result:ReferenciaDevolucion) => {
                      if(result){
                        this.pagos.push(result);
                        this.pendingAmount = this.amount - this.pagos.map(p=>p.monto).reduce((a,b)=>a+b,0);
                      }});
  }


  public closeDialog(ref:NbDialogRef<any>, data ?: ReferenciaDevolucion){
    if(data){
      ref.close(data);
    } else {
      ref.close(undefined)
    }
  }

}
