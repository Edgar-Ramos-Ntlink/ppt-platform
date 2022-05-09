import { Component, Input, OnInit } from "@angular/core";

@Component({
  selector: "nt-invoice-status",
  templateUrl: "./invoice-status.component.html",
  styleUrls: ["./invoice-status.component.scss"],
})
export class InvoiceStatusComponent implements OnInit {
  @Input() public status: string;

  constructor() {}

  ngOnInit(): void {}
}
