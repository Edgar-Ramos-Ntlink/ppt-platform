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
import { Devolucion } from '../../../models/devolucion';
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
    }

    public removeClient(index: number) {
        this.return.clientes.splice(index, 1);
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
                    const client = this.return.clientes[0];
                    this.return.porcentajeContacto = client.porcentajeContacto;
                    this.return.montoContacto =
                        (this.return.total * client.porcentajeContacto) / 116;
                    this.return.procentajeCliente = client.porcentajeCliente;
                    this.return.montoCliente =
                        (this.return.total * client.porcentajeCliente +
                            this.return.total * 100) /
                        116;
                    this.return.porcentajeDespacho = client.porcentajeDespacho;
                    this.return.montoDespacho =
                        (this.return.total * client.porcentajeDespacho) / 116;
                    this.return.porcentajePromotor = client.porcentajePromotor;
                    this.return.montoPromotor =
                        (this.return.total * client.porcentajePromotor) / 116;
                } else {
                    console.log('No result exit dialog');
                }
            });
    }
}
