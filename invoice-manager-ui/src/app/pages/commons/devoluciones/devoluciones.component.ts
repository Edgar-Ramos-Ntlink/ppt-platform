import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'nt-devoluciones',
  templateUrl: './devoluciones.component.html',
  styleUrls: ['./devoluciones.component.scss']
})
export class DevolucionesComponent implements OnInit {

  public loading: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
