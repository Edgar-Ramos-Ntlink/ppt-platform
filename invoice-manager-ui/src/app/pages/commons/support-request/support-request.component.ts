import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { AppConstants } from '../../../models/app-constants';
import { ResourceFile } from '../../../models/resource-file';
import { SupportData } from '../../../@core/data/support-data';
import { SupportRequest } from '../../../models/support-request';
import { NtError } from '../../../@core/models/nt-error';
import { map, switchMap, catchError, finalize } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

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
    public modules: [];

    constructor(
        private supportService: SupportData,
        private notificationService: NotificationsService,
        private downloadService: DonwloadFileService,
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
    ) {
        this.supportForm = this.formBuilder.group({
            contactPhone: [
                '',
                [
                    Validators.required,
                    Validators.pattern('^((\\+..-?)|0)?[0-9]{10}$'),
                ],
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
                    Validators.maxLength(300),
                ],
            ],
            supportType: ['*', [Validators.minLength(2), Validators.maxLength(300)]],
            module: ['*', [Validators.minLength(2), Validators.maxLength(300)]],
            notes: ['', [Validators.minLength(2), Validators.maxLength(300)]],
            solution: ['', [Validators.maxLength(300)]],
            agent: [''],
            dueDate: [''],
            product: [
                'SJ INVOICE MANAGER',
                [Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN)],
            ],
            clientEmail: [
                sessionStorage.getItem('email'),
                [Validators.required, Validators.email],
            ],
            status: [''],
        });
    }

    ngOnInit(): void {
        console.log('onInitSoporte');
        this.route.paramMap.subscribe((route) => {
            this.dataFile = undefined;
            this.folio = route.get('folio');
            console.log('folio', this.folio);
            this.modules = JSON.parse(sessionStorage.getItem('user'))?.roles;
            if (this.folio !== '*' && this.folio != null) {
                this.loading = true;
                this.supportService
                    .buscarSoporte(+this.folio)
                    .pipe(
                        catchError((error: NtError) => {
                            this.notificationService.sendNotification(
                                'danger',
                                error.message,
                                'No se encontro informacion',
                            );
                            this.supportForm.reset();
                            this.supportForm.patchValue(
                                new SupportRequest(
                                    sessionStorage.getItem('email'),
                                ),
                            );
                            return EMPTY;
                        }),
                        finalize(() => this.loading = false),
                    )
                    .subscribe(
                        (support) => {
                            console.log(support);
                            this.supportForm.patchValue(support);
                        },
                    );
            } else {
                this.dataFile = undefined;
                this.supportForm.reset();
                this.supportForm.patchValue(
                    new SupportRequest(sessionStorage.getItem('email')),
                );
            }
        });
    }

    public async onSubmit() {
        try {
            this.loading = true;
            const support: SupportRequest = { ...this.supportForm.value };
            support.product = 'SJ INVOICE MANAGER';
            support.companyName = 'SEMEEL JAK, S.A. DE C.V.';
            support.companyRfc = 'SJA121128SG5';
            support.clientEmail = sessionStorage.getItem('email');
            support.contactEmail = sessionStorage.getItem('email');
            support.agent = 'soporte.invoice@ntlink.com.mx';
            support.supportLevel = 'primer nivel';
            support.requestType = 'soporte';
            support.status = 'PENDIENTE';
            const result: SupportRequest = await this.supportService
                .insertSoporte(support)
                .toPromise();
            if (this.dataFile) {
                await this.supportService
                    .insertAttachedFile(result.folio, this.dataFile)
                    .toPromise();
            }
            this.notificationService.sendNotification(
                'success',
                `Solicitud creada con folio ${result.folio}`,
                'Solicitud creada',
            );
            this.router.navigate([`/pages/soporte/${result.folio}`]);
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error.message,
                'Error en la solicitud',
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
                    file.name.length,
                );
                this.dataFile.data = reader.result.toString();
            };
            reader.onerror = (error) => {
                this.notificationService.sendNotification(
                    'danger',
                    'Error',
                    'Error cargando el archivo',
                );
            };
        }
    }

    public async uploadFile(folio: number): Promise<void> {
        try {
            this.loading = true;
            this.dataFile.tipoRecurso = 'SOPORTE';
            this.dataFile.referencia = folio.toString();
            this.dataFile.tipoArchivo = 'DOCUMENT';

            await this.supportService
                .insertAttachedFile(folio, this.dataFile)
                .toPromise();
            this.notificationService.sendNotification(
                'info',
                'El archivo se cargo correctamente',
            );
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error cargando archivo',
            );
        }
        this.loading = false;
    }

    public downloadFile() {
        this.downloadService.downloadFile(
            this.dataFile.data,
            `archivoAdjunto${this.dataFile.extension}`,
            this.dataFile.formato,
        );
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
    get clientEmail() {
        // tslint:disable-next-line:no-non-null-assertion
        return this.supportForm.get('clientEmail')!;
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
}
