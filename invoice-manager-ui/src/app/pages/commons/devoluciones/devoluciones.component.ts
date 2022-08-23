import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NbDialogService } from '@nebular/theme';
import { map } from 'rxjs/operators';
import { ClientsData } from '../../../@core/data/clients-data';
import { UsersData } from '../../../@core/data/users-data';
import { NtError } from '../../../@core/models/nt-error';
import { User } from '../../../@core/models/user';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { Client } from '../../../models/client';
import { Devolucion, ReferenciaDevolucion } from '../../../models/devolucion';
import { GenericPage } from '../../../models/generic-page';
import { SeleccionPagosComponent } from './seleccion-pagos/seleccion-pagos.component';

@Component({
    selector: 'nt-devoluciones',
    templateUrl: './devoluciones.component.html',
    styleUrls: ['./devoluciones.component.scss'],
})
export class DevolucionesComponent implements OnInit {
    public loading: boolean = false;

    public usersCat: User[] = [];
    public clientsCat: Client[] = [];
    public return: Devolucion = new Devolucion();

    constructor(
        private notificationService: NotificationsService,
        private dialogService: NbDialogService,
        private usersService: UsersData,
        private clientsService: ClientsData,
        private route: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe((route) => {
            const id = route.get('id');
            if (id !== '*') {
                this.loadReturnInfo(+id);
            } else {
                this.usersService
                    .getUsers(0, 1000, { status: '1' })
                    .pipe(
                        map((p: GenericPage<User>) => {
                            const users = p.content;
                            users.forEach(
                                (u) => (u.name = `${u.alias} - ${u.email}`)
                            );
                            return users;
                        })
                    )
                    .subscribe(
                        (users) => (this.usersCat = users),
                        (error: NtError) =>
                            this.notificationService.sendNotification(
                                'danger',
                                error.message,
                                'Error cargando promotores'
                            )
                    );
            }
        });
    }

    public loadReturnInfo(id: number) {
        console.log('Recovering return info for', id);
    }

    public selectPromotor(user: User) {
        this.return.promotor = user.email;
        this.clientsService
            .getClientsByPromotor(user.email)
            .pipe(
                map((clients: Client[]) => {
                    clients.forEach(
                        (c) => (c.notas = `${c.rfc} - ${c.razonSocial}`)
                    );
                    return clients;
                })
            )
            .subscribe(
                (clients) => (this.clientsCat = clients),
                (error: NtError) =>
                    this.notificationService.sendNotification(
                        'danger',
                        error.message,
                        'Error cargando clientes'
                    )
            );
    }

    public selectClient(client: Client) {
        this.return.clientes.push(client);
        const mainClient = this.return.clientes[0];
        this.return.porcentajeContacto = mainClient.porcentajeContacto;
        this.return.porcentajeDespacho = mainClient.porcentajeDespacho;
        this.return.porcentajePromotor = mainClient.porcentajePromotor;
        this.return.procentajeCliente = mainClient.porcentajeCliente;
    }

    public removeClient(index: number) {
        this.return.clientes.splice(index, 1);
        if (index === 0) {
            // if there is not assigned clients then payments needs to be removed
            this.return.pagos = [];
        }
    }

    public searchPayments() {
        this.dialogService
            .open(SeleccionPagosComponent, {
                context: {
                    devolucion: this.return,
                },
            })
            .onClose.subscribe((result: any[]) => {
                if (result) {
                    result.forEach((p) => this.return.pagos.push(p));
                    this.return.total = this.return.pagos
                        .map((p) => p.monto)
                        .reduce((a, b) => a + b);
                    this.calculateAmounts();
                } else {
                    console.log('No result exit dialog');
                }
            });
    }

    private calculateAmounts() {
        this.return.montoPromotor =
            (this.return.total * this.return.porcentajePromotor) / 116;
        this.return.montoDespacho =
            (this.return.total * this.return.porcentajeDespacho) / 116;
        this.return.montoContacto =
            (this.return.total * this.return.porcentajeContacto) / 116;
        this.return.comisionCliente =
            (this.return.total * this.return.procentajeCliente) / 116;
        this.return.pasivoCliente = this.return.total / 1.16;
        this.return.montoCliente =
            this.return.total -
            this.return.montoPromotor -
            this.return.montoContacto -
            this.return.montoDespacho;

        if(!this.return.detalles.find(d=>'DESPACHO')){
            const detail = new ReferenciaDevolucion('DESPACHO',this.return.montoDespacho);
            detail.formaPago = 'OTRO';;
            detail.notas = '';
            this.return.detalles.push(detail);
        }
    }
}
