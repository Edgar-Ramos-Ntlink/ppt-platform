import { Component, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';

@Component({
  selector: 'ngx-observacion-pendientes',
  templateUrl: './observacion-pendientes.component.html',
  styleUrls: ['./observacion-pendientes.component.scss']
})
export class ObservacionPendientesComponent implements OnInit {

  constructor(
    protected ref: NbDialogRef<ObservacionPendientesComponent>,
  ) { }

  exit() {
    this.ref.close();
  }

  ngOnInit() {
  }

}
