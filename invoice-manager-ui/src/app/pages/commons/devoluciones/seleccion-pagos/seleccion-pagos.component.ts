import { Component, Input, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { ReturnsUtilsService } from '../../../../@core/util-services/returns-utils.service';
import {
    Devolucion,
    ReferenciaDevolucion,
} from '../../../../models/devolucion';

@Component({
    selector: 'nt-seleccion-pagos',
    templateUrl: './seleccion-pagos.component.html',
    styleUrls: ['./seleccion-pagos.component.scss'],
})
export class SeleccionPagosComponent implements OnInit {
    @Input() public devolucion: Devolucion;

    public pagos: any[] = [];
    public loading: boolean = false;

    constructor(
        protected ref: NbDialogRef<SeleccionPagosComponent>,
        private returnUtils: ReturnsUtilsService
    ) {}

    ngOnInit(): void {
        this.loadPayments();
    }

    private loadPayments() {
        this.pagos = [
            {
                id: 1,
                seleccionado: false,
                razonSocialEmpresa: 'ABASTECEDORA GEMCO',
                rfcEmpresa: 'AGE2112158F4',
                folio: '2002636373766242',
                banco: 'SANTANDER',
                fechaPago: '2022-10-23',
                monto: 4500.67,
            },
            {
                id: 2,
                seleccionado: false,
                razonSocialEmpresa: 'ALASTORE MEXICO',
                rfcEmpresa: 'AME140512D80',
                folio: '2002636675278737',
                banco: 'HSBC',
                fechaPago: '2022-09-21',
                monto: 9500.67,
            },
            {
                id: 3,
                seleccionado: false,
                razonSocialEmpresa: 'AXKAN PUBLICIDAD',
                rfcEmpresa: 'APU140519728',
                folio: '2002636725216217',
                banco: 'AZTECA',
                fechaPago: '2022-12-20',
                monto: 14500.67,
            },
            {
                id: 4,
                seleccionado: false,
                razonSocialEmpresa: 'BLAKE INGENIERIA INTEGRAL',
                rfcEmpresa: 'BII180413413',
                folio: '2002639097363912',
                banco: 'BBVA',
                fechaPago: '2022-07-05',
                monto: 5500.67,
            },
            {
                id: 5,
                seleccionado: false,
                razonSocialEmpresa: 'CONSTRUCTORA URBANA APIRO',
                rfcEmpresa: 'CUA210415MC1',
                folio: '2002630236537281',
                banco: 'SANTANDER',
                fechaPago: '2022-05-29',
                monto: 47500.67,
            },
            {
                id: 6,
                seleccionado: false,
                razonSocialEmpresa: 'CORPORATIVO NOVUM PROCESSAR',
                rfcEmpresa: 'CNP101126SL8',
                folio: '2002638391917178',
                banco: 'BANORTE',
                fechaPago: '2022-03-10',
                monto: 98000.67,
            },
            {
                id: 7,
                seleccionado: false,
                razonSocialEmpresa: 'ENVISION ACTION GROUP',
                rfcEmpresa: 'EAG211209FP3',
                folio: '2000385673821192',
                banco: 'INBURSA',
                fechaPago: '2022-05-12',
                monto: 3300.33,
            },
        ];
    }

    public exit() {
        this.ref.close();
    }

    public addPayments() {
        let r: Devolucion = JSON.parse(JSON.stringify(this.devolucion));
        const selectedPayments = this.pagos.filter((p) => p.seleccionado);
        selectedPayments.forEach((p) => {
            // removing duplicated payments
            if (!this.devolucion.pagos.find((d) => d.id === p.id)) {
                r.pagos.push(p);
            }
        });
        r = this.returnUtils.calculateAmounts(r);
        
        this.ref.close(r);
    }
}
