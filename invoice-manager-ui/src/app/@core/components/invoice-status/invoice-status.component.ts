import { Component, Input, OnInit } from "@angular/core";
import { Factura } from "../../models/factura";

@Component({
  selector: "nt-invoice-status",
  templateUrl: "./invoice-status.component.html",
  styleUrls: ["./invoice-status.component.scss"],
})
export class InvoiceStatusComponent implements OnInit {
  @Input() public factura: Factura;
  @Input() public isAdmin: boolean;

  constructor() {}

  ngOnInit(): void {}
}
