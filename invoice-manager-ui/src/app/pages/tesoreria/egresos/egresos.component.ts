import { Component, OnInit } from '@angular/core';
import { GenericPage } from '../../../models/generic-page';
import { PaymentsData } from '../../../@core/data/payments-data';

@Component({
    selector: 'ngx-egresos',
    templateUrl: './egresos.component.html',
    styleUrls: ['./egresos.component.scss'],
})
export class EgresosComponent implements OnInit {
    public filterParams: any = {
        formaPago: '*',
        status: 'PAGADO',
        banco: '*',
        since: '',
        to: '',
    };
    public errors: string[] = [];
    public page: GenericPage<any> = new GenericPage();
    public pageSize = '10';
    public total: number = 0.0;

    constructor(private paymentService: PaymentsData) {}

    ngOnInit() {
        this.updateDataTable();
        this.filterParams = {
            formaPago: '*',
            status: 'PAGADO',
            banco: '*',
            since: '',
            to: '',
        };
    }

    public updateDataTable(currentPage?: number, pageSize?: number) {
        const pageValue = currentPage || 0;
        const sizeValue = pageSize || 10;
        this.paymentService
            .getExpenses(pageValue, sizeValue, this.filterParams)
            .subscribe((result: GenericPage<any>) => (this.page = result));
        this.paymentService
            .getExpensesSum()
            .subscribe((total) => (this.total = total));
    }

    public onChangePageSize(pageSize: number) {
        this.updateDataTable(this.page.number, pageSize);
    }

    public downloadHandler() {
        alert('Descarga no implementada');
        /*this.paymentService.getExpenses(0, 10000, this.filterParams).subscribe(result => {
    });*/
    }
}
