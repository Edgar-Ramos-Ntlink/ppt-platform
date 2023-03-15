import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { AppConstants } from '../../../models/app-constants';
import { ResourceFile } from '../../../models/resource-file';
import { SupportData } from '../../../@core/data/support-data';
import { SupportRequest } from '../../../models/support-request';
import { NtError } from '../../../@core/models/nt-error';
import { catchError, finalize, map } from 'rxjs/operators';
import { EMPTY } from 'rxjs';
import { FilesData } from '../../../@core/data/files-data';
import { NbDialogService } from '@nebular/theme';
import { UsersData } from '../../../@core/data/users-data';
import { User } from '../../../@core/models/user';

@Component({
    selector: 'nt-support-request',
    templateUrl: './support-request.component.html',
    styleUrls: ['./support-request.component.scss'],
})
export class SupportRequestComponent implements OnInit {
    public supportForm: FormGroup;
    public dataFile: ResourceFile;
    public loading: boolean = false;
    public folio: string = '*';
    public folioBusqueda: string = '';
    public modules: string[];
    public supportView = false;
    public adminView = false;
    public supportUsers: User[] = [];

    constructor(
        private supportService: SupportData,
        private userService: UsersData,
        private filesService: FilesData,
        private dialogService: NbDialogService,
        private notificationService: NotificationsService,
        private downloadService: DonwloadFileService,
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.supportForm = this.formBuilder.group({
            contactPhone: [
                '',
                [Validators.pattern('^((\\+..-?)|0)?[0-9]{10}$')],
            ],
            contactName: [
                '',
                [
                    Validators.required,
                    Validators.maxLength(100),
                    Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
                ],
            ],
            problem: [
                '',
                [
                    Validators.required,
                    Validators.minLength(20),
                    Validators.maxLength(2000),
                ],
            ],
            errorMessage: [
                '',
                [Validators.minLength(2), Validators.maxLength(2000)],
            ],
            module: [
                '*',
                [Validators.minLength(2), Validators.maxLength(2000)],
            ],
            notes: ['', [Validators.minLength(2), Validators.maxLength(2000)]],
            solution: [
                '',
                [Validators.minLength(10), Validators.maxLength(2000)],
            ],
            supportType: [
                '*',
                [Validators.minLength(2), Validators.maxLength(2000)],
            ],
            agent: [''],
            dueDate: [''],
            creation: [''],
            update: [''],
            product: [
                'SJ INVOICE MANAGER',
                [Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN)],
            ],
            contactEmail: [
                sessionStorage.getItem('email'),
                [Validators.required, Validators.email],
            ],
            status: [''],
        });
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe((route) => {
            this.dataFile = undefined;
            this.folio = route.get('folio');
            const user: User = JSON.parse(sessionStorage.getItem('user'));

            this.modules = user?.roles.map((r) => r.role);

            this.adminView = this.router.url.includes('pages/administracion');
            this.supportView = this.router.url.includes('pages/soporte');

            if (this.folio !== '*' && this.folio != null) {
                this.loading = true;
                this.supportService
                    .buscarSoporte(+this.folio)
                    .pipe(
                        catchError((error: NtError) => {
                            this.notificationService.sendNotification(
                                'danger',
                                error.message,
                                'No se encontro informacion'
                            );
                            this.dataFile = undefined;
                            const support = new SupportRequest(user.email);
                            support.contactName = user.name;
                            this.supportForm.reset();
                            this.supportForm.patchValue(support);
                            return EMPTY;
                        }),
                        finalize(() => (this.loading = false))
                    )
                    .subscribe((support) => {
                        this.supportForm.patchValue(support);
                        this.filesService
                            .getResourceFile(this.folio, 'SOPORTE', 'DOCUMENT')
                            .subscribe((data) => (this.dataFile = data));
                    });
            } else {
                this.dataFile = undefined;
                const support = new SupportRequest(user.email);
                support.contactName = user.name;
                this.supportForm.reset();
                this.supportForm.patchValue(support);
            }
        });
        this.userService
            .getUsers(0, 20, { role: 'SOPORTE' })
            .pipe(map((page) => page.content))
            .subscribe((users) => (this.supportUsers = users));
    }

    public async onSubmit() {
        try {
            this.loading = true;
            const support: SupportRequest = { ...this.supportForm.value };
            support.contactEmail = sessionStorage.getItem('email');
            support.agent = 'soporte.invoice@ntlink.com.mx';
            support.supportLevel = 'primer nivel';
            support.requestType = 'soporte';
            support.status = 'PENDIENTE';
            const result: SupportRequest = await this.supportService
                .insertSoporte(support)
                .toPromise();

            if (this.dataFile) {
                this.dataFile.referencia = result.folio.toString();
                this.dataFile.tipoRecurso = 'SOPORTE';
                this.dataFile.tipoArchivo = 'DOCUMENT';
                await this.filesService
                    .insertResourceFile(this.dataFile)
                    .toPromise();
            }
            this.notificationService.sendNotification(
                'success',
                `Solicitud creada con folio ${result.folio}`,
                'Solicitud creada'
            );
            this.router.navigate([`/pages/solicitud/${result.folio}`]);
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error.message,
                'Error en la solicitud'
            );
        }
        this.loading = false;
    }

    public fileDataUploadListener(event: any): void {
        const reader = new FileReader();
        this.dataFile = new ResourceFile();
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            reader.readAsDataURL(file);
            reader.onload = () => {
                this.dataFile.fileName = file.name;
                this.dataFile.extension = file.name.substring(
                    file.name.lastIndexOf('.'),
                    file.name.length
                );
                this.dataFile.data = reader.result.toString();
            };
            reader.onerror = (error) => {
                this.notificationService.sendNotification(
                    'danger',
                    'Error',
                    'Error cargando el archivo'
                );
            };
        }
    }

    public downloadFile() {
        this.downloadService.downloadFile(
            this.dataFile.data,
            `${this.dataFile.tipoRecurso}_${this.dataFile.referencia}${this.dataFile.extension}`,
            this.dataFile.formato
        );
    }

    public takeSupport() {
        this.loading = true;
        const support: SupportRequest = { ...this.supportForm.value };
        support.agent = sessionStorage.getItem('email');
        support.status = 'EN PROGRESO';
        support.folio = +this.folio;
        this.supportService.updateSoporte(+this.folio, support).subscribe(
            (result) => {
                this.supportForm.patchValue(result);
                this.notificationService.sendNotification(
                    'success',
                    'El soporte se te ha asignado',
                    'Soporte asignado'
                );
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error asignando el soporte'
                );
                this.loading = false;
            }
        );
    }

    public finalizeSupport() {
        this.loading = true;
        const support: SupportRequest = { ...this.supportForm.value };
        support.status = 'FINALIZADO';
        support.folio = +this.folio;
        this.supportService.updateSoporte(+this.folio, support).subscribe(
            (result) => {
                this.supportForm.patchValue(result);
                this.notificationService.sendNotification(
                    'success',
                    'El soporte se ha cerrado',
                    'Soporte Finalizado'
                );
                this.loading = false;
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error asignando el soporte'
                );
                this.loading = false;
            }
        );
    }

    public validateSupport() {
        const support: SupportRequest = { ...this.supportForm.value };
        support.status = 'VALIDACION';
        support.folio = +this.folio;
        if (!support.solution || support.solution.length < 10) {
            this.notificationService.sendNotification(
                'warning',
                'Es necesaria la descripcion de la solucion',
                'Datos faltantes'
            );
            return;
        }
        this.loading = true;
        this.supportService.updateSoporte(+this.folio, support).subscribe(
            (result) => {
                this.supportForm.patchValue(result);
                this.loading = false;
                this.notificationService.sendNotification(
                    'success',
                    'El soporte se ha modificado al estatus de validacion',
                    'Soporte en validacion'
                );
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error asignando el soporte'
                );
                this.loading = false;
            }
        );
    }

    public rejectSupport() {
        const support: SupportRequest = { ...this.supportForm.value };
        support.status = 'EN PROGRESO';
        support.folio = +this.folio;
        if (!support.notes || support.notes.length < 10) {
            this.notificationService.sendNotification(
                'warning',
                'Describir en la seccion de notas la razon del rechazo',
                'Datos faltantes'
            );
            return;
        }
        this.loading = true;
        this.supportService.updateSoporte(+this.folio, support).subscribe(
            (result) => {
                this.supportForm.patchValue(result);
                this.loading = false;
                this.notificationService.sendNotification(
                    'success',
                    'El soporte se ha rechazado',
                    'Soporte rechazado'
                );
            },
            (error: NtError) => {
                this.notificationService.sendNotification(
                    'danger',
                    error.message,
                    'Error asignando el soporte'
                );
                this.loading = false;
            }
        );
    }

    public scaleSupport(dialog: TemplateRef<any>) {
        const support: SupportRequest = { ...this.supportForm.value };
        support.dueDate = new Date();
        this.dialogService
            .open(dialog, { context: support })
            .onClose.subscribe((soporte) => {
                if (soporte !== undefined) {
                    this.loading = true;
                    soporte.status = 'EN PROGRESO';
                    soporte.folio = +this.folio;
                    this.supportService
                        .updateSoporte(+this.folio, soporte)
                        .subscribe(
                            (result) => {
                                this.supportForm.patchValue(result);
                                this.loading = false;
                                this.notificationService.sendNotification(
                                    'success',
                                    'El soporte ha escalado',
                                    'Soporte escalado'
                                );
                            },
                            (error: NtError) => {
                                this.notificationService.sendNotification(
                                    'danger',
                                    error.message,
                                    'Error asignando el soporte'
                                );
                                this.loading = false;
                            }
                        );
                }
            });
    }

    public findTicket() {
        this.router.navigate([`/pages/soporte/${this.folioBusqueda}`]);
    }

    get contactPhone() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('contactPhone')!;
    }
    get contactName() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('contactName')!;
    }
    get problem() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('problem')!;
    }
    get errorMessage() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('errorMessage')!;
    }
    get supportType() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('supportType')!;
    }
    get module() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('module')!;
    }
    get notes() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('notes')!;
    }
    get solution() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('solution')!;
    }
    get product() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('product')!;
    }
    get contactEmail() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('contactEmail')!;
    }
    get agent() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('agent')!;
    }
    get dueDate() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('dueDate')!;
    }
    get status() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('status')!;
    }
    get creation() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('creation')!;
    }
    get update() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('update')!;
    }
}
