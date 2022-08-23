import { Component, Input, OnInit } from '@angular/core';
import { DetalleDevolucion, Devolucion, ReferenciaDevolucion, TipoDevolucion } from '../../../../models/devolucion';

@Component({
  selector: 'nt-detalle-devolucion',
  templateUrl: './detalle-devolucion.component.html',
  styleUrls: ['./detalle-devolucion.component.scss']
})
export class DetalleDevolucionComponent implements OnInit {

  @Input() public tipo: TipoDevolucion;
  @Input() public devolucion : Devolucion;

  public detalle: DetalleDevolucion;

  constructor() { }

  ngOnInit(): void {
    this.detalle = this.devolucion.detalles.find(d=>this.tipo === d.tipo);
  }

}
