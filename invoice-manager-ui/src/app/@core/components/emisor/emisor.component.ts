import { Component, Input, OnInit } from "@angular/core";
import { Store } from "@ngrx/store";
import { RegimenFiscal } from "../../../models/catalogos/regimen-fiscal";
import { AppState } from "../../../reducers";
import { updateEmisor, updateEmisorAddress } from "../../core.actions";
import { CatalogsData } from "../../data/catalogs-data";
import { Emisor } from "../../models/cfdi/emisor";

@Component({
  selector: "nt-emisor",
  templateUrl: "./emisor.component.html",
  styleUrls: ["./emisor.component.scss"],
})
export class EmisorComponent implements OnInit {
  @Input() public emisor: Emisor;
  @Input() public allowEdit: Boolean;
  @Input() public direccion: string;

  public regimenes: RegimenFiscal[] = [];

    constructor(
        private catalogsService: CatalogsData,
        private store: Store<AppState>
    ) {}

  ngOnInit(): void {
    this.catalogsService
            .getAllRegimenFiscal()
            .then((reg) => (this.regimenes = reg));
  }

  public addressChange(address) {
    this.store.dispatch(updateEmisorAddress({ address }));
}

public updateRegimenFiscal(value: string) {
    const emisor = { ...this.emisor, regimenFiscal: value };
    this.store.dispatch(updateEmisor({ emisor }));
}

public updateRazonSocial(value: string) {
    const emisor = { ...this.emisor, nombre: value };
    this.store.dispatch(updateEmisor({ emisor }));
}
}
