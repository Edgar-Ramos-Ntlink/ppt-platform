import { Component, Input, OnInit } from "@angular/core";
import { Emisor } from "../../models/cfdi/emisor";

@Component({
  selector: "nt-emisor",
  templateUrl: "./emisor.component.html",
  styleUrls: ["./emisor.component.scss"],
})
export class EmisorComponent implements OnInit {
  @Input() public emisor: Emisor;

  @Input() public direccion: string;

  constructor() {}

  ngOnInit(): void {}
}
