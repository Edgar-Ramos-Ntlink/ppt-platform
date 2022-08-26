import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import {
    Devolucion,
    ReferenciaDevolucion,
    TipoDevolucion,
} from '../../models/devolucion';

@Injectable({
    providedIn: 'root',
})
export class ReturnsUtilsService {
    constructor() {}


    public calculateAmounts(devolucion: Devolucion): Devolucion {
        devolucion.total = devolucion.pagos
            .map((p) => p.monto)
            .reduce((a, b) => a + b, 0);
        devolucion.montoPromotor =
            (devolucion.total * devolucion.porcentajePromotor) / 116;
        devolucion.montoDespacho =
            (devolucion.total * devolucion.porcentajeDespacho) / 116;
        devolucion.montoContacto =
            (devolucion.total * devolucion.porcentajeContacto) / 116;
        devolucion.comisionCliente =
            (devolucion.total * devolucion.procentajeCliente) / 116;
        devolucion.pasivoCliente = devolucion.total / 1.16;
        devolucion.montoCliente =
            devolucion.total -
            devolucion.montoPromotor -
            devolucion.montoContacto -
            devolucion.montoDespacho;

        if (
            devolucion.detalles.length > 0 &&
            devolucion.detalles[0].receptorPago === 'DESPACHO'
        ) {
            devolucion.detalles.shift();
        }
        if (devolucion.montoDespacho > 0) {
            const detail = new ReferenciaDevolucion(
                'DESPACHO',
                devolucion.montoDespacho
            );
            detail.formaPago = 'OTRO';
            detail.notas = '';
            devolucion.detalles.unshift(detail);
        }

        return devolucion;
    }

    public getAmountByType(type: TipoDevolucion, dev: Devolucion): number {
        let amount = 0;
        switch (type) {
            case 'PROMOTOR':
                amount = dev.montoPromotor;
                break;
            case 'CLIENTE':
                amount = dev.montoCliente;
                break;
            case 'CONTACTO':
                amount = dev.montoContacto;
                break;
            case 'DESPACHO':
                amount = dev.montoDespacho;
                break;
            default : 
                amount = 0;
                break;
        }
        return amount;
    }
}
