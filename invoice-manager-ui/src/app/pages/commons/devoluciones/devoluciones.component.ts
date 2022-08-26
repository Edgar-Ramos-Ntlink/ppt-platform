import { Component, OnInit } from '@angular/core';
import {
    AbstractControl,
    AsyncValidatorFn,
    FormControl,
    FormGroup,
    ValidationErrors,
    Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NbDialogService } from '@nebular/theme';
import { select, Store } from '@ngrx/store';
import { map } from 'rxjs/operators';
import { ClientsData } from '../../../@core/data/clients-data';
import { UsersData } from '../../../@core/data/users-data';
import { NtError } from '../../../@core/models/nt-error';
import { User } from '../../../@core/models/user';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { ReturnsUtilsService } from '../../../@core/util-services/returns-utils.service';
import { Client } from '../../../models/client';
import { Devolucion, TipoDevolucion } from '../../../models/devolucion';
import { GenericPage } from '../../../models/generic-page';
import { AppState } from '../../../reducers';
import { updateReturn } from '../commons.actions';
import { returnSelector } from '../commons.selectors';
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
    public returnForm = new FormGroup({
        cliente: new FormControl('', {
            validators: [
                Validators.required,
                Validators.max(10),
                Validators.min(0),
            ],
            asyncValidators: [this.ValidateAmmounts()],
            updateOn:'blur'
        }),
        promotor: new FormControl('', {
            validators: [
                Validators.required,
                Validators.max(10),
                Validators.min(0),
            ],
            asyncValidators: [this.ValidateAmmounts()],
            updateOn:'blur'
        }),
        contacto: new FormControl('', {
            validators: [
                Validators.required,
                Validators.max(10),
                Validators.min(0),
            ],
            asyncValidators: [this.ValidateAmmounts()],
            updateOn:'blur'
        }),
        despacho: new FormControl('', {
            validators: [Validators.max(16), Validators.min(2)],
            asyncValidators: [this.ValidateAmmounts()],
            updateOn:'blur'
        }),
    });

    constructor(
        private notificationService: NotificationsService,
        private dialogService: NbDialogService,
        private returnUtils: ReturnsUtilsService,
        private usersService: UsersData,
        private clientsService: ClientsData,
        private route: ActivatedRoute,
        private store: Store<AppState>
    ) {}

    ngOnInit(): void {
        this.route.paramMap.subscribe((route) => {
            this.store.dispatch(updateReturn({ return: new Devolucion() }));
            const id = route.get('id');
            if (id !== '*') {
                this.loadReturnInfo(+id);
            } else {
                this.loadPromotorInfo();
            }
        });

        this.store
            .pipe(select(returnSelector))
            .subscribe((result) => (this.return = result));
    }

    public ValidateAmmounts(): AsyncValidatorFn {
        return (control: AbstractControl): Promise<ValidationErrors> => {
            const user : User = JSON.parse(sessionStorage.getItem('user'));
            const limit = user.roles.map(r => r.role).includes('ADMINISTRADOR')?20:16;
            const sum = (+this.return.porcentajeContacto) +
                (+this.return.porcentajeDespacho) +
                (+this.return.porcentajePromotor) +
                (+this.return.procentajeCliente) ;
            console.log(`commision sum : ${sum} limit is ${limit}`);
            return new Promise((resolve) => (sum<=limit)
                    ? resolve(null)
                    : resolve({ invalidamounts: true })
            );
        };
    }

    private loadPromotorInfo() {
        this.usersService
            .getUsers(0, 1000, { status: '1' })
            .pipe(
                map((p: GenericPage<User>) => {
                    const users = p.content;
                    users.forEach((u) => (u.name = `${u.alias} - ${u.email}`));
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

    private loadReturnInfo(id: number) {
        console.log('Recovering return info for', id);
    }

    public selectPromotor(user: User) {
        this.store.dispatch(
            updateReturn({ return: { ...this.return, promotor: user.email } })
        );
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
        const r: Devolucion = JSON.parse(JSON.stringify(this.return));
        r.clientes.push(client);
        r.porcentajeContacto = r.clientes[0].porcentajeContacto || 0;
        r.porcentajeDespacho = r.clientes[0].porcentajeDespacho || 0;
        r.porcentajePromotor = r.clientes[0].porcentajePromotor || 0;
        r.procentajeCliente = r.clientes[0].porcentajeCliente || 0;
        this.returnForm.get('promotor').setValue(r.porcentajePromotor);
        this.returnForm.get('cliente').setValue(r.procentajeCliente);
        this.returnForm.get('contacto').setValue(r.porcentajeContacto);
        this.store.dispatch(
            updateReturn({ return: this.returnUtils.calculateAmounts(r) })
        );
    }

    public removeClient(index: number) {
        let r: Devolucion = JSON.parse(JSON.stringify(this.return));
        r.clientes.splice(index, 1);
        if (index === 0) {
            // if there is not assigned clients then payments needs to be removed
            r.pagos = [];
            r = this.returnUtils.calculateAmounts(r);
        }
        this.store.dispatch(updateReturn({ return: r }));
    }

    public searchPayments() {
        this.dialogService
            .open(SeleccionPagosComponent, {
                context: {
                    devolucion: this.return,
                },
            })
            .onClose.subscribe((result: Devolucion) => {
                if (result) {
                    this.store.dispatch(updateReturn({ return: result }));
                }
            });
    }

    public removePayment(index: number) {
        let r: Devolucion = JSON.parse(JSON.stringify(this.return));
        r.pagos.splice(index, 1);
        r = this.returnUtils.calculateAmounts(r);
        this.store.dispatch(updateReturn({ return: r }));
    }

    public refreshAmounts(value: number, type: TipoDevolucion) {
        console.log(type, value);
        const r: Devolucion = JSON.parse(JSON.stringify(this.return));
        switch (type) {
            case 'PROMOTOR':
                r.porcentajePromotor = value;
                break;
            case 'CLIENTE':
                r.procentajeCliente = value;
                break;
            case 'CONTACTO':
                r.porcentajeContacto = value;
                break;
            case 'DESPACHO':
                r.porcentajeDespacho = value;
        }
        this.store.dispatch(
            updateReturn({ return: this.returnUtils.calculateAmounts(r) })
        );
    }

    get promotor() {
        return this.returnForm.get('promotor')!;
    }

    get cliente() {
        return this.returnForm.get('cliente')!;
    }

    get contacto() {
        return this.returnForm.get('contacto')!;
    }
}
