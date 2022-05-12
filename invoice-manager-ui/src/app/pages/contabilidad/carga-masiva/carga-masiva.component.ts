import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import * as XLSX from 'xlsx';
import { CompaniesData } from '../../../@core/data/companies-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { Empresa } from '../../../models/empresa';
import { Cfdi } from '../../../@core/models/cfdi/cfdi';
import { Concepto } from '../../../@core/models/cfdi/concepto';
import { Factura } from '../../../@core/models/factura';
import { NbToastrService } from '@nebular/theme';
import { AppConstants } from '../../../models/app-constants';

@Component({
    selector: 'ngx-carga-masiva',
    templateUrl: './carga-masiva.component.html',
    styleUrls: ['./carga-masiva.component.scss'],
})
export class CargaMasivaComponent implements OnInit {
    @ViewChild('fileInput', { static: true })
    public fileInput: ElementRef;
    public invoices: any[] = [];
    public loading: boolean = false;
    public params: any = {
        lineaRetiro: 'A',
        lineaDeposito: 'B',
        filename: '',
        dataValid: false,
    };

    private companies: any = {};

    constructor(
        private companyService: CompaniesData,
        private cfdiValidator: CfdiValidatorService,
        private invoiceService: InvoicesData,
        private toastrService: NbToastrService,
    ) {}

    ngOnInit() {
        this.params = {
            lineaRetiro: 'A',
            lineaDeposito: 'B',
            filename: '',
            dataValid: false,
        };
        this.companies = {};
    }

    onFileChange(files) {
        let workBook = null;
        let jsonData = null;
        const reader = new FileReader();
        const file = files[0];
        this.companies = {};
        this.params.filename = file.name;
        reader.onload = (event) => {
            const data = reader.result;
            workBook = XLSX.read(data, { type: 'binary' });
            jsonData = workBook.SheetNames.reduce((initial, name) => {
                const sheet = workBook.Sheets[name];
                initial[name] = XLSX.utils.sheet_to_json(sheet);
                return initial;
            }, {});
            if (jsonData.CARGA_MASIVA === undefined) {
                alert('Formato Excel invalido');
            } else {
                this.loading = true;
                this.invoices = jsonData.CARGA_MASIVA;
                this.getCompaniesInfo(this.invoices);
            }
        };
        reader.readAsBinaryString(file);
    }

    calcularTotal() {
        if (this.invoices === undefined || this.invoices.length === 0) {
            return 0;
        } else {
            return this.invoices
                .map((t) => t.TOTAL)
                .reduce((total, m) => total + m);
        }
    }

    clean() {
        this.invoices = [];
        this.companies = {};
        this.params.dataValid = false;
        this.params.filename = '';
        this.fileInput.nativeElement.value = '';
    }

    public async validarInformacion() {
        this.loading = true;
        this.params.dataValid = true;
        if (this.invoices !== undefined && this.invoices.length > 0) {
            for (const transfer of this.invoices) {
                transfer.observaciones = [];

                // VALIDACIONES EMISOR
                if (this.companies[transfer.RFC_EMISOR] === undefined) {
                    transfer.observaciones.push(
                        `${transfer.RFC_EMISOR} no esta dada de alta en el sistema`
                    );
                } else if (
                    this.companies[transfer.RFC_EMISOR].tipo !==
                    this.params.lineaDeposito
                ) {
                    transfer.observaciones.push(
                        `${transfer.RFC_EMISOR} no es de tipo ${this.params.lineaDeposito}`
                    );
                } else if (!this.companies[transfer.RFC_EMISOR].activo) {
                    transfer.observaciones.push(
                        `${transfer.RFC_EMISOR} no se encuentra activa`
                    );
                } else if(this.companies[transfer.RFC_EMISOR].regimenFiscal === undefined || this.companies[transfer.RFC_EMISOR].regimenFiscal === '*') {
                    transfer.observaciones.push(
                        `${transfer.RFC_EMISOR} no cuenta con regimen fiscal`
                    );
                }

                // VALIDACIONES RECEPTOR
                if (this.companies[transfer.RFC_RECEPTOR] === undefined) {
                    transfer.observaciones.push(
                        `${transfer.RFC_RECEPTOR} no esta dada de alta en el sistema`
                    );
                } else if (
                    this.companies[transfer.RFC_RECEPTOR].tipo !==
                    this.params.lineaRetiro
                ) {
                    transfer.observaciones.push(
                        `${transfer.RFC_RECEPTOR} no es de tipo ${this.params.lineaRetiro}`
                    );
                } else if (!this.companies[transfer.RFC_RECEPTOR].activo) {
                    transfer.observaciones.push(
                        `${transfer.RFC_RECEPTOR} no se encuentra activa`
                    );
                } else if(this.companies[transfer.RFC_RECEPTOR].regimenFiscal === undefined || this.companies[transfer.RFC_RECEPTOR].regimenFiscal === '*') {
                    transfer.observaciones.push(
                        `${transfer.RFC_RECEPTOR} no cuenta con regimen fiscal`
                    );
                }

                if(isNaN(transfer.CLAVE_PROD_SERVICIO)){
                    transfer.observaciones.push(
                        `La clave producto servicio ${transfer.CLAVE_PROD_SERVICIO} es invalida`);
                }


                if (transfer.observaciones.length === 0) {
                    const fact = await this.buildFacturaFromTransfer(
                        transfer,
                        this.companies[transfer.RFC_EMISOR],
                        this.companies[transfer.RFC_RECEPTOR]
                    );
                    if(fact.notas!== undefined && fact.notas.length>0){
                        transfer.observaciones.push(fact.notas);
                    }
                    transfer.observaciones.push(
                        ...this.cfdiValidator.validarConcepto(
                            fact.cfdi.conceptos[0]
                        )
                    );
                    transfer.observaciones.push(
                        ...this.cfdiValidator.validarCfdi(fact.cfdi)
                    );
                }
                if (transfer.observaciones.length > 0) {
                    this.params.dataValid = false;
                } 
            }
            if(this.params.dataValid){this.toastrService.success('Se han validado correctamente todas las facturas',"Facturas Validas",AppConstants.TOAST_CONFIG);}
        } else {
            this.params.dataValid = false;
            this.toastrService.warning('No se encontro informacion cargada o valida',"No hay datos",AppConstants.TOAST_CONFIG);
        }
        this.loading = false;
    }

    cargarFacturas() {
        this.loading = true;
        this.loadfactura(this.invoices);
    }

    private async loadfactura(invoices: any) {
        for (const invoice of invoices) {
            invoice.observaciones = [];
            const factura = await this.buildFacturaFromTransfer(
                invoice,
                this.companies[invoice.RFC_EMISOR],
                this.companies[invoice.RFC_RECEPTOR]
            );
                try {
                    // TODO evaluate if descripcionCUPS is required
                    //factura.cfdi.conceptos[0].descripcionCUPS = claves[0].descripcion;
                    await this.invoiceService
                        .insertNewInvoice(factura)
                        .toPromise();
                } catch (error) {
                    invoice.observaciones.push(error?.message || 'Error desconocido');
                }
        }
        this.loading = false;
    }

    private async getCompaniesInfo(invoices: any[]) {
        for (const transfer of invoices) {
            try {
                const emisor = <Empresa>(
                    await this.companyService
                        .getCompanyByRFC(transfer.RFC_EMISOR)
                        .toPromise()
                );
                this.companies[transfer.RFC_EMISOR] = emisor;
                const receptor = <Empresa>(
                    await this.companyService
                        .getCompanyByRFC(transfer.RFC_RECEPTOR)
                        .toPromise()
                );
                this.companies[transfer.RFC_RECEPTOR] = receptor;
            } catch (error) {
                console.error(error);
                transfer.observaciones =
                    error.error.message || 'Error desconocido';
            }
        }
        this.loading = false;
    }

    private async buildFacturaFromTransfer(
        transfer: any,
        emisorCompany: Empresa,
        receptorCompany: Empresa
    ): Promise<Factura> {
        const factura = new Factura();
        try{
        
        factura.rfcEmisor = transfer.RFC_EMISOR;
        factura.razonSocialEmisor = emisorCompany.razonSocial;
        factura.lineaEmisor = this.params.lineaDeposito;
        factura.rfcRemitente = transfer.RFC_RECEPTOR;
        factura.razonSocialRemitente = receptorCompany.razonSocial;
        factura.lineaRemitente = this.params.lineaRetiro;
        factura.metodoPago = transfer.METODO_PAGO;
        factura.statusFactura = '8';
        factura.solicitante = sessionStorage.getItem('email');
        const cfdi = new Cfdi();
        cfdi.receptor.nombre = receptorCompany.razonSocial;
        cfdi.receptor.rfc = transfer.RFC_RECEPTOR;
        cfdi.receptor.domicilioFiscalReceptor = receptorCompany.cp;
        cfdi.receptor.regimenFiscalReceptor = receptorCompany.regimenFiscal;
        cfdi.receptor.usoCfdi = 'P01';
        factura.direccionEmisor =
            this.cfdiValidator.generateCompanyAddress(receptorCompany);
        cfdi.emisor.nombre = emisorCompany.razonSocial;

        cfdi.emisor.rfc = transfer.RFC_EMISOR;
        cfdi.emisor.regimenFiscal = emisorCompany.regimenFiscal;
        cfdi.formaPago = transfer.FORMA_PAGO.toString();
        factura.direccionReceptor =
            this.cfdiValidator.generateCompanyAddress(emisorCompany);
        cfdi.moneda = 'MXN';
        cfdi.total = transfer.TOTAL;
        cfdi.subtotal = transfer.IMPORTE;
        cfdi.metodoPago = transfer.METODO_PAGO.toString();
        cfdi.lugarExpedicion = emisorCompany.cp;
        const concepto = new Concepto();
        concepto.cantidad = transfer.CANTIDAD;
        concepto.claveProdServ = transfer.CLAVE_PROD_SERVICIO;

        concepto.claveUnidad = transfer.CLAVE_UNIDAD;
        concepto.descripcion = transfer.CONCEPTO;
        concepto.unidad = transfer.UNIDAD;
        concepto.valorUnitario = transfer.PRECIO_UNITARIO;
        concepto.importe = transfer.IMPORTE;
        this.cfdiValidator.validarConcepto(concepto);
        cfdi.conceptos.push(
            this.cfdiValidator.buildConcepto(concepto, true, false)
        );
        factura.cfdi = await this.cfdiValidator.calcularImportes(cfdi);
        return factura;
        }catch(error){
            this.toastrService.danger(error?.message,"Error en la validacion del CFDI",AppConstants.TOAST_CONFIG);
            factura.notas = `Error en la construccion de la factura de ${transfer.RFC_EMISOR} para  ${transfer.RFC_RECEPTOR} : ${error.message}`;
        }
    }
}
