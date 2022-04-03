import { Component, OnInit, Input, TemplateRef, Output, EventEmitter } from '@angular/core';
import { UsoCfdi } from '../../../models/catalogos/uso-cfdi';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CfdiData } from '../../../@core/data/cfdi-data';
import { HttpErrorResponse } from '@angular/common/http';
import { NbDialogService } from '@nebular/theme';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { FilesData } from '../../../@core/data/files-data';
import { Router } from '@angular/router';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { Cfdi } from '../../../@core/models/cfdi/cfdi';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Concepto } from '../../../@core/models/cfdi/concepto';

@Component({
  selector: 'ngx-cfdi',
  templateUrl: './cfdi.component.html',
  styleUrls: ['./cfdi.component.scss'],
})
export class CfdiComponent implements OnInit {


  @Input() cfdi: Cfdi;
  @Input() pagos: Pago[];
  @Input() allowEdit: Boolean;
  
  @Input() module: string = '';

  @Output() cfdiEvent = new EventEmitter<string>();

  public errorMessagesCdfi: string[] = []; 
  public loading: boolean = false;

  // auxiliar variables
  public newConcep: Concepto;
  public successMessage: string;

  // catalogs
  public usoCfdiCat: UsoCfdi[] = [];
  public payTypeCat: Catalogo[] = [];

  constructor(
    private catalogsService: CatalogsData,
    private filesService: FilesData,
    private downloadService: DonwloadFileService,
    private cfdiservice: CfdiData,
    private invoiceService : InvoicesData,
    private router: Router,
    private dialogService: NbDialogService) {
    }

  ngOnInit() {
    // catalogs info
    this.initVariables();
  }

  initVariables(){
    this.catalogsService.getAllUsoCfdis().then(cat => this.usoCfdiCat = cat);
    this.catalogsService.getFormasPago().then(cat => this.payTypeCat = cat);
    this.successMessage = undefined;
    this.newConcep = new Concepto();
    this.loading = false;
  }

  onPayMethodSelected(clave: string) {
    this.catalogsService.getFormasPago(clave)
      .then(cat => {
        this.payTypeCat = cat;
        this.cfdi.formaPago = this.payTypeCat[0].id;
        if (clave === 'PPD') {
          this.cfdi.formaPago = '99';
        } else {
          this.cfdi.formaPago = '01';
        }
      });
  }

  onMonedaChange(moneda: string) {
    if (moneda === 'MXN') {
      this.cfdi.tipoCambio = 1.00;
    }
  }

  updateCfdi(dialog: TemplateRef<any>) {
    this.loading = true;
    this.successMessage = undefined;
    this.errorMessagesCdfi = [];
    //validacion direccion
    if(this.cfdi.emisor.direccion === undefined || this.cfdi.emisor.direccion === '') {
      this.errorMessagesCdfi.push('La direccion del emisor es un valor solicitado');
      this.loading = false;
      return;
    }
    if(this.cfdi.receptor.direccion === undefined || this.cfdi.receptor.direccion === '') {
      this.errorMessagesCdfi.push('La direccion del receptor es un valor solicitado');
      this.loading = false;
      return;
    }

    this.cfdiservice.updateCfdi(this.cfdi)
      .subscribe(cfdi => {
        this.cfdi = cfdi;
        this.loading = false;
        this.successMessage = 'CFDI actualizado correctamente';
        this.cfdiEvent.emit(this.cfdi.id.toString());
      } , (error: HttpErrorResponse) => {
        this.loading = false;
        this.dialogService.open(dialog,
          { context: (error.error != null && error.error !== undefined) ?
          error.error.message : `${error.statusText} : ${error.message}`});
      });
  }

  public downloadPdf(folio: string) {
    this.filesService.getFacturaFile(folio, 'PDF').subscribe(
      file => this.downloadService.downloadFile(file.data,
        `${folio}-${this.cfdi.emisor.nombre}-${this.cfdi.receptor.nombre}.pdf`, 'application/pdf;')
    );
  }
  public downloadXml(folio: string) {
    this.filesService.getFacturaFile(folio, 'XML').subscribe(
      file => this.downloadService.downloadFile(file.data,
        `${folio}-${this.cfdi.emisor.nombre}-${this.cfdi.receptor.nombre}.xml`, 'text/xml;charset=utf8;')
    );
  }

  public redirectToCfdi(folio: string){
    this.invoiceService.getInvoiceByFolio(folio)
      .toPromise().then((fact)=>this.router.navigate([`./pages/promotor/precfdi/${fact.idCfdi}`]));
  }

  public redirectToChildCfdi(id: number){
   this.router.navigate([`./pages/promotor/precfdi/${id}`]);
  }

  public validacionDireccion(){}



}
