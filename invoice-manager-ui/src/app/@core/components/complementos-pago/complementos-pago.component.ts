import { Component, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { cfdi, complementos } from '../../core.selectors';
import { Cfdi } from '../../models/cfdi/cfdi';
import { ComplementoPago } from '../../models/complemento-pago';

@Component({
  selector: 'nt-complementos-pago',
  templateUrl: './complementos-pago.component.html',
  styleUrls: ['./complementos-pago.component.scss']
})
export class ComplementosPagoComponent implements OnInit {

  
  public cfdi:Cfdi;
  public complementos: ComplementoPago[];

  constructor(private store: Store<AppState>) { }

  ngOnInit(): void {
    this.store.pipe(select(cfdi)).subscribe((cfdi)=>this.cfdi = cfdi);
    this.store.pipe(select(complementos)).subscribe((complementos)=>this.complementos = complementos);
  }

}
