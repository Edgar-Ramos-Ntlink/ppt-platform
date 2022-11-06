import { Injectable } from '@angular/core';
import { CatalogsData } from '../data/catalogs-data';
import { UsoCfdi } from '../../models/catalogos/uso-cfdi';
import { subtract, bignumber, multiply, format } from 'mathjs';
import { CfdiService } from '../back-services/cfdi.service';
import { Empresa } from '../../models/empresa';
import { Concepto } from '../models/cfdi/concepto';
import { Cfdi } from '../models/cfdi/cfdi';
import { Traslado } from '../models/cfdi/traslado';
import { Retencion } from '../models/cfdi/retencion';
import { Impuesto } from '../models/cfdi/impuesto';

@Injectable({
    providedIn: 'root',
})
export class CfdiValidatorService {
    private metodoPagoCat: string[] = ['PUE', 'PPD'];
    private formaPagoCat: string[] = [
        '01',
        '02',
        '03',
        '04',
        '05',
        '06',
        '08',
        '12',
        '13',
        '14',
        '15',
        '17',
        '23',
        '24',
        '25',
        '26',
        '27',
        '28',
        '29',
        '30',
        '31',
        '99',
    ];

    private usoCfdiCat: string[] = [];

    constructor(
        private catService: CatalogsData,
        private cfdiService: CfdiService
    ) {
        this.catService
            .getAllUsoCfdis()
            .then(
                (cat: UsoCfdi[]) => (this.usoCfdiCat = cat.map((c) => c.clave))
            );
    }

    public buildConcepto(concepto: Concepto): Concepto {
        concepto.descuento = concepto.descuento || 0;
        const tipoImpuesto = concepto.impuesto.split('_')[0];

        concepto.importe = +format(
            multiply(
                bignumber(concepto.cantidad),
                bignumber(concepto.valorUnitario)
            )
        );
        const base = +format(
            subtract(bignumber(concepto.importe), bignumber(concepto.descuento))
        );
        concepto.impuestos = [];
        const impuesto = new Impuesto();
        if (concepto.objetoImp != '01') {
            if(tipoImpuesto === 'IVA'){
                const tasaImp = +concepto.impuesto.split('_')[1];
            const imp = +format(multiply(base, bignumber(tasaImp)));
            impuesto.traslados = [
                new Traslado('002', 'Tasa', tasaImp.toString(), base, imp),
            ];
            }
            if (tipoImpuesto === 'RET') {
                const tasaRet = +concepto.impuesto.split('_')[1];
                const retencion = +format(multiply(base, bignumber(tasaRet))); // TODO calcular retencion dinamicamente
                impuesto.retenciones = [
                    new Retencion('002', 'Tasa', tasaRet.toString(), base, retencion),
                ];
            }
            concepto.impuestos.push(impuesto);
        } else {
           concepto.impuestos = null;
        }
        return concepto;
    }

    public validarConcepto(concepto: Concepto): string[] {
        const messages: string[] = [];
        if (concepto.cantidad <= 0) {
            messages.push('La cantidad requerida debe ser mayor a 0');
        }
        if (
            concepto.claveProdServ === undefined ||
            concepto.claveProdServ === '' ||
            concepto.claveProdServ.length == 0
        ) {
            messages.push(
                'La clave producto servicio del concepto es un valor requerido.'
            );
        }
        if (
            concepto.claveUnidad === undefined ||
            concepto.claveUnidad === '*'
        ) {
            messages.push(
                'La clave de unidad y la unidad son campos requeridos.'
            );
        }
        if (
            concepto.descripcion === undefined ||
            concepto.descripcion.length < 1
        ) {
            messages.push('La descripción del concepto es un valor requerido.');
        }
        if (concepto.valorUnitario <= 0) {
            messages.push(
                'El valor unitario del  concepto no puede ser menor igual a 0 pesos.'
            );
        }
        return messages;
    }

    public calcularImportes(cfdi: Cfdi): Promise<Cfdi> {
        return this.cfdiService.recalculateCfdi(cfdi).toPromise();
    }

    public generateAddress(contribuyente: any) {
        let address = `${contribuyente.calle}`.trim();
        if (
            contribuyente.noExterior !== undefined &&
            contribuyente.noExterior !== null
        ) {
            address += ' ';
            address += `${contribuyente.noExterior}`.trim();
        }
        if (
            contribuyente.noInterior !== undefined &&
            contribuyente.noInterior !== null
        ) {
            address += `,${contribuyente.noInterior}`.trim();
        }
        if (
            contribuyente.localidad !== undefined &&
            contribuyente.localidad !== null
        ) {
            address += `,${contribuyente.localidad}`.trim();
        }
        if (
            contribuyente.municipio !== undefined &&
            contribuyente.municipio != null
        ) {
            address += `,${contribuyente.municipio}`.trim();
        }
        if (
            contribuyente.estado !== undefined &&
            contribuyente.estado !== null
        ) {
            address += `,${contribuyente.estado}`.trim();
        }
        if (
            contribuyente.estado !== undefined &&
            contribuyente.estado !== null
        ) {
            address += `,C.P. ${contribuyente.cp}`.trim();
        }
        return address.toUpperCase().trim();
    }

    public generateCompanyAddress(company: Empresa) {
        let address = `${company.calle}`.trim();
        if (company.noExterior !== undefined && company.noExterior !== null) {
            address += ' ';
            address += `${company.noExterior}`.trim();
        }
        if (company.noInterior !== undefined && company.noInterior !== null) {
            address += `,${company.noInterior}`.trim();
        }
        if (company.colonia !== undefined && company.colonia !== null) {
            address += `,${company.colonia}`.trim();
        }
        if (company.municipio !== undefined && company.municipio != null) {
            address += `,${company.municipio}`.trim();
        }
        if (company.estado !== undefined && company.estado !== null) {
            address += `,${company.estado}`.trim();
        }
        if (company.estado !== undefined && company.estado !== null) {
            address += `,C.P. ${company.cp}`.trim();
        }
        return address.toUpperCase().trim();
    }

    public validarCfdi(cfdi: Cfdi): string[] {
        const messages: string[] = [];
        if (
            cfdi.receptor === undefined ||
            cfdi.receptor.rfc === undefined ||
            cfdi.receptor.rfc.length < 11 ||
            cfdi.receptor.nombre.length == 0
        ) {
            messages.push('La información del receptor es un valor solicitado');
        }
        if (
            cfdi.emisor === undefined ||
            cfdi.emisor.rfc === undefined ||
            cfdi.emisor.rfc.length < 11 ||
            cfdi.emisor.nombre.length == 0
        ) {
            messages.push('La información del emisor es un valor solicitado');
        }
        if (cfdi.emisor.rfc === cfdi.receptor.rfc) {
            messages.push('El emisor y receptor son la misma entidad');
        }
        if (cfdi.receptor.regimenFiscalReceptor === undefined || cfdi.receptor.regimenFiscalReceptor === '*'){
            messages.push('Es necesario asignar el régimen fiscal del receptor');
        }
        if (cfdi.lugarExpedicion === undefined || cfdi.lugarExpedicion === '00000' || cfdi.lugarExpedicion.length === 0){
            messages.push('El código postal del lugar de expedicion es un valor requerido');
        }
        if (
            cfdi.receptor.usoCfdi === undefined ||
            cfdi.receptor.usoCfdi === '*'
        ) {
            messages.push('El uso del CFDI es un campo requerido.');
        } else if (
            this.usoCfdiCat.find((e) => e === cfdi.receptor.usoCfdi) ===
            undefined
        ) {
            messages.push(
                `El uso de cfdi ${cfdi.receptor.usoCfdi} no es valido.`
            );
        }
        if (cfdi.moneda === undefined || cfdi.moneda === '*') {
            messages.push('La moneda es un campo requerido.');
        }
        if (cfdi.formaPago === undefined || cfdi.formaPago === '*') {
            messages.push('La forma de pago es un campo requerido.');
        } else if (
            this.formaPagoCat.find((e) => e === cfdi.formaPago) === undefined
        ) {
            messages.push(`La forma de pago ${cfdi.formaPago} no es valida.`);
        }
        if (cfdi.metodoPago === undefined || cfdi.metodoPago === '*') {
            messages.push('El metodo de pago es un campo requerido.');
        } else if (
            this.metodoPagoCat.find((e) => e === cfdi.metodoPago) === undefined
        ) {
            messages.push(`El metodo de pago ${cfdi.metodoPago} no es valido.`);
        }
        if (cfdi.conceptos.length < 1) {
            messages.push(
                'La factura debe contener a menos 1 concepto a declarar.'
            );
        }
        if (cfdi.moneda !== 'MXN' && cfdi.tipoCambio === 1.0) {
            messages.push(
                `El tipo de cambio para  ${cfdi.moneda} no puede ser igual a $1.00`
            );
        }
        if (cfdi.moneda === 'MXN' && cfdi.tipoCambio !== 1.0) {
            messages.push(
                'Si la moneda seleccionada es MXN el tipo de cambio debe ser $1.00.'
            );
        }
        if(cfdi.metodoPago === 'PPD' && cfdi.formaPago !== '99'){
            messages.push('En facturas PPD el metodo de pago debe ser siempre POR DEFINIR')
        }
        if(cfdi.metodoPago === 'PUE' && cfdi.formaPago === '99'){
            messages.push('En facturas PUE el metodo de pago no puede ser POR DEFINIR')
        }
        return messages;
    }

    public validRegExpAphaNumeric(input: string): boolean {
        const regexp = new RegExp(`^([A-Za-z0-9ÁÉÍÓÚÑáéíóúñ.,'&\\-\\s]+)$`);
        return regexp.test(input);
    }
}
