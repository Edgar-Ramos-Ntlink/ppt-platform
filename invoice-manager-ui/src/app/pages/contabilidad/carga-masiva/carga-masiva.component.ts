import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import * as XLSX from 'xlsx';
import { CompaniesData } from '../../../@core/data/companies-data';
import { CfdiValidatorService } from '../../../@core/util-services/cfdi-validator.service';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { Empresa } from '../../../models/empresa';
import { UsersData } from '../../../@core/data/users-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { User } from '../../../@core/models/user';
import { Cfdi } from '../../../@core/models/cfdi/cfdi';
import { Concepto } from '../../../@core/models/cfdi/concepto';
import { Factura } from '../../../@core/models/factura';

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
    public user: User;
    public errorMessages: string[] = [];
    private companies: any = {};

    constructor(
        private companyService: CompaniesData,
        private cfdiValidator: CfdiValidatorService,
        private invoiceService: InvoicesData,
        private catalogsData: CatalogsData,
        private userService: UsersData
    ) {}

    ngOnInit() {
        this.userService
            .getUserInfo()
            .then((user) => (this.user = user as User));
        this.params = {
            lineaRetiro: 'A',
            lineaDeposito: 'B',
            filename: '',
            dataValid: false,
        };
        this.companies = {};
        this.errorMessages = [];
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
        this.errorMessages = [];
        this.fileInput.nativeElement.value = '';
        this.params.successMessage = undefined;
    }

    public async validarInformacion() {
        this.loading = true;
        this.params.successMessage = undefined;
        this.params.dataValid = true;
        this.errorMessages = [];
        if (this.invoices !== undefined && this.invoices.length > 0) {
            for (const transfer of this.invoices) {
                transfer.observaciones = [];
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
                }
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
                } else if (!this.companies[transfer.RFC_EMISOR].activo) {
                    transfer.observaciones.push(
                        `${transfer.RFC_RECEPTOR} no se encuentra activa`
                    );
                }
                if (transfer.observaciones.length === 0) {
                    const fact = await this.buildFacturaFromTransfer(
                        transfer,
                        this.companies[transfer.RFC_EMISOR],
                        this.companies[transfer.RFC_RECEPTOR]
                    );
                    transfer.observaciones.push(
                        ...this.cfdiValidator.validarConcepto(
                            fact.cfdi.conceptos[0]
                        )
                    );
                    transfer.observaciones.push(
                        ...this.cfdiValidator.validarCfdi(fact.cfdi)
                    );
                }
                if (transfer.observaciones.length === 0) {
                    transfer.observaciones = 'VALIDO';
                } else {
                    this.params.dataValid = false;
                }
            }
        } else {
            this.params.dataValid = false;
            this.errorMessages.push(
                'No se encontro informacion cargada o valida'
            );
        }
        this.loading = false;
    }

    cargarFacturas() {
        this.loading = true;
        this.params.successMessage = undefined;
        this.errorMessages = [];
        this.loadfactura(this.invoices);
    }

    private async loadfactura(invoices: any) {
        for (const invoice of invoices) {
            const factura = await this.buildFacturaFromTransfer(
                invoice,
                this.companies[invoice.RFC_EMISOR],
                this.companies[invoice.RFC_RECEPTOR]
            );
            const claveProdServ = +invoice.CLAVE_PROD_SERVICIO;
            if (!isNaN(claveProdServ)) {
                try {
                    const claves =
                        await this.catalogsData.getProductoServiciosByClave(
                            claveProdServ.toString()
                        );
                    factura.cfdi.conceptos[0].claveProdServ =
                        claveProdServ.toString();
                    // TODO evaluate if descripcionCUPS is required
                    //factura.cfdi.conceptos[0].descripcionCUPS = claves[0].descripcion;
                    await this.invoiceService
                        .insertNewInvoice(factura)
                        .toPromise();
                    invoice.observaciones = 'CARGADA';
                } catch (error) {
                    invoice.observaciones =
                        error.error.message || 'Error desconocido';
                }
            } else {
                invoice.observaciones =
                    'La clave producto servicio es invalida.';
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
        factura.rfcEmisor = transfer.RFC_EMISOR;
        factura.razonSocialEmisor = emisorCompany.razonSocial;
        factura.lineaEmisor = this.params.lineaDeposito;
        factura.rfcRemitente = transfer.RFC_RECEPTOR;
        factura.razonSocialRemitente = receptorCompany.razonSocial;
        factura.lineaRemitente = this.params.lineaRetiro;
        factura.metodoPago = transfer.METODO_PAGO;
        factura.statusFactura = '8';
        factura.solicitante = this.user.email;
        const cfdi = new Cfdi();
        cfdi.receptor.nombre = receptorCompany.razonSocial;
        cfdi.receptor.rfc = transfer.RFC_RECEPTOR;
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
    }
}
