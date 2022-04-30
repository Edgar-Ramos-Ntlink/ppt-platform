import { Component, Input, OnInit } from "@angular/core";
import { UsoCfdi } from "../../../models/catalogos/uso-cfdi";
import { CatalogsData } from "../../data/catalogs-data";
import { Receptor } from "../../models/cfdi/receptor";

@Component({
  selector: "nt-receptor",
  templateUrl: "./receptor.component.html",
  styleUrls: ["./receptor.component.scss"],
})
export class ReceptorComponent implements OnInit {
  @Input() public receptor: Receptor;
  @Input() public allowEdit: Boolean;
  @Input() public direccion: string;

  // catalogs
  public usoCfdiCat: UsoCfdi[] = [];

  constructor(private catalogsService: CatalogsData) {}

  ngOnInit(): void {
    this.catalogsService
      .getAllUsoCfdis()
      .then((cat) => (this.usoCfdiCat = cat));
  }
}
