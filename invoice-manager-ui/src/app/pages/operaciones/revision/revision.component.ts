import { Component, OnInit, TemplateRef } from '@angular/core';
import { NbDialogService } from '@nebular/theme';
import { ClientsData } from '../../../@core/data/clients-data';
import { Client } from '../../../models/client';
import { InvoicesData } from '../../../@core/data/invoices-data';
import { ActivatedRoute } from '@angular/router';
import { Pago } from '../../../@core/models/cfdi/pago';
import { Factura } from '../../../@core/models/factura';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { NtError } from '../../../@core/models/nt-error';
import { initInvoice, updateInvoice } from '../../../@core/core.actions';
import { invoice } from '../../../@core/core.selectors';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

@Component({
    selector: 'ngx-revision',
    templateUrl: './revision.component.html',
    styleUrls: ['./revision.component.scss'],
})
export class RevisionComponent implements OnInit {
    public folio: string;
    public pagosCfdi: Pago[] = [];
    public factura: Factura = new Factura();

    public soporte: boolean = false;
    public loading: boolean = true;

    constructor(
        private clientsService: ClientsData,
        private invoiceService: InvoicesData,
        private notificationService: NotificationsService,
        private dialogService: NbDialogService,
        private route: ActivatedRoute,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        this.route.paramMap.subscribe((route) => {
            const folio = route.get('folio');
            this.loading = true;
            this.getInvoiceByFolio(folio);
            this.folio = folio;
        });

        this.store
            .pipe(select(invoice))
            .subscribe((fact) => (this.factura = fact));
    }

    ngOnDestroy() {
        this.store.dispatch(initInvoice({ invoice: new Factura() }));
    }

    public getInvoiceByFolio(folio: string) {
        this.pagosCfdi = [];
        this.invoiceService.getInvoiceByFolio(folio).subscribe(
            (invoice) => {
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error'
                );
                this.store.dispatch(initInvoice({ invoice: new Factura() }));
                this.loading = false;
            }
        );
    }

    public linkInvoice(factura: Factura) {
        this.loading = true;
        const fact = { ...this.factura };
        this.invoiceService.generateReplacement(factura.folio, fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'El documento relacionado se ha generado exitosamente',
                    'Documento relacionado'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error en la sustitucion'
                );
                this.loading = false;
            }
        );
    }

    public generateCreditNoteInvoice(factura: Factura) {
        this.loading = true;
        const fact = { ...factura };
        this.invoiceService.generateCreditNote(factura.folio, fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'La nota de credito se ha generado exitosamente',
                    'Nota credito creada'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error creando la nota de crédito'
                );
                this.loading = false;
            }
        );
    }

    public aceptarFactura() {
        const fact = JSON.parse(JSON.stringify(this.factura));
        fact.validacionOper = true;
        if (fact.metodoPago === 'PUE' && fact.validacionTeso == false) {
            fact.statusFactura = '2';
        }
        if (fact.metodoPago === 'PPD' || (fact.metodoPago === 'PUE' && fact.validacionTeso == true)) {
            fact.statusFactura = '4';
        }
        this.loading = true;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.notificationService.sendNotification(
                    'success',
                    'factura aceptada'
                );
                this.store.dispatch(updateInvoice({ invoice }));
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error'
                );
                this.loading = false;
            }
        );
    }

    public async rechazarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact: Factura = JSON.parse(JSON.stringify(factura));
            fact.statusDetail = 'Campos inválidos en el CFDI';
            fact.notas = `Factura rechazada por operaciones : ${sessionStorage.getItem(
                'email'
            )}`;
            this.dialogService
                .open(dialog, { context: fact })
                .onClose.subscribe((result) => {
                    this.loading = true;
                    if (result !== undefined) {
                        result.statusFactura = '6'; // update to rechazo operaciones
                        this.invoiceService.updateInvoice(result).subscribe(
                            (invoice) => {
                                this.notificationService.sendNotification(
                                    'success',
                                    'factura rechazada'
                                );
                                this.store.dispatch(updateInvoice({ invoice }));
                                this.loading = false;
                            },
                            (error: NtError) => {
                                this.notificationService.sendNotification(
                                    'danger',
                                    error?.message,
                                    'Error'
                                );
                                this.loading = false;
                            }
                        );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error.message,
                'Error en el rechazo'
            );
            this.loading = false;
        }
    }

    public async timbrarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact = { ...factura };

            let client: Client = await this.clientsService
                .getClientsByPromotorAndRfc(
                    this.factura.solicitante,
                    this.factura.cfdi.receptor.rfc
                )
                .toPromise();

            if (client.activo) {
                this.dialogService
                    .open(dialog, { context: fact })
                    .onClose.subscribe((invoice) => {
                        this.loading = true;
                        if (invoice !== undefined) {
                            this.invoiceService
                                .timbrarFactura(fact.folio, invoice)
                                .subscribe(
                                    (invoice) => {
                                        this.notificationService.sendNotification(
                                            'success',
                                            'factura timbrada'
                                        );
                                        this.store.dispatch(
                                            updateInvoice({ invoice })
                                        );
                                        this.loading = false;
                                    },
                                    (error: NtError) => {
                                        this.notificationService.sendNotification(
                                            'danger',
                                            error?.message,
                                            'Error al timbrar'
                                        );
                                        this.loading = false;
                                    }
                                );
                        } else {
                            this.loading = false;
                        }
                    });
            } else {
                this.notificationService.sendNotification(
                    'danger',
                    'El cliente que solicita la factura se encuentra inactivo',
                    'Cliente inactivo'
                );
                this.loading = false;
            }
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error.message,
                'Error'
            );
            this.loading = false;
        }
    }

    public async cancelarFactura(factura: Factura, dialog: TemplateRef<any>) {
        try {
            const fact = JSON.parse(JSON.stringify(factura));
            fact.motivo = '02';
            this.dialogService
                .open(dialog, { context: fact })
                .onClose.subscribe((result) => {
                    this.loading = true;
                    if (result !== undefined) {
                        this.invoiceService
                            .cancelarFactura(fact.folio, result)
                            .subscribe(
                                (invoice) => {
                                    this.notificationService.sendNotification(
                                        'success',
                                        'factura cancelada'
                                    );
                                    this.store.dispatch(
                                        updateInvoice({ invoice })
                                    );
                                    this.loading = false;
                                },
                                (error: NtError) => {
                                    this.notificationService.sendNotification(
                                        'danger',
                                        error?.message,
                                        'Error'
                                    );
                                    this.loading = false;
                                }
                            );
                    } else {
                        this.loading = false;
                    }
                });
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error.message,
                'Error'
            );
            this.loading = false;
        }
    }

    public async revalidateInvoice() {
        this.loading = true;
        const fact: Factura = JSON.parse(JSON.stringify(this.factura));
        fact.statusFactura = '1';
        fact.validacionOper = false;
        fact.total = this.factura.cfdi.total;
        fact.metodoPago = this.factura.cfdi.metodoPago;
        this.invoiceService.updateInvoice(fact).subscribe(
            (invoice) => {
                this.loading = false;
                this.store.dispatch(updateInvoice({ invoice }));
                this.notificationService.sendNotification(
                    'success',
                    'actualización exitosa',
                    'CFDI Revalidado'
                );
            },
            (error: NtError) => {
                this.loading = false;
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error en la revalidacion'
                );
            }
        );
    }
}
