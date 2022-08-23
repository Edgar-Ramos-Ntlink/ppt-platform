import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { Devolucion, ReferenciaDevolucion, TipoDevolucion } from '../../../../models/devolucion';

@Component({
  selector: 'nt-detalle-devolucion',
  templateUrl: './detalle-devolucion.component.html',
  styleUrls: ['./detalle-devolucion.component.scss']
})
export class DetalleDevolucionComponent implements OnInit {

  @Input() public type: TipoDevolucion;
  @Input() public return : Devolucion;
  @Input() public amount : number;
  @Input() public allowEdit : boolean;

  public pagos: ReferenciaDevolucion[] = [];
  
  constructor( private dialogService: NbDialogService) { }

  ngOnInit(): void {
    this.return.detalles.filter(d=>this.type === d.receptorPago)
    .forEach(e=>this.pagos.push(e));
  }

  public addReturnPayment(dialog:TemplateRef<any>){
   
    this.dialogService
                    .open(dialog, { context: new ReferenciaDevolucion(this.type,+this.amount.toFixed(2)) })
                    .onClose.subscribe((result:ReferenciaDevolucion) => {this.return.pagos.push(result); this.pagos.push(result)})

  }

}
