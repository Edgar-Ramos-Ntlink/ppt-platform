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

    constructor(
        private supportService: SupportData,
        private notificationService: NotificationsService,
        private downloadService: DonwloadFileService,
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.supportForm = this.formBuilder.group({
            contactPhone: [
                '',
                [
                    Validators.required,
                    Validators.pattern('^((\\+..-?)|0)?[0-9]{10}$'),
                ],
            ],
            contactName: ['', [Validators.required, Validators.maxLength(100)]],
            problem: ['', [Validators.required, Validators.maxLength(300)]],
            notes: ['*', [Validators.minLength(2), Validators.maxLength(300)]],
            solution: ['', [Validators.maxLength(300)]],
            agent: [''],
            dueDate: [''],
            product: [
                'SJ INVOICE MANAGER',
                [
                    Validators.required,
                    Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
                ],
            ],
            contactEmail: [
                sessionStorage.getItem('email'),
                [Validators.required],
            ],
        });
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe((route) => {
            this.folio = route.get('folio');
            if (this.folio !== '*') {
                this.supportService.buscarSoporte(+this.folio).subscribe(
                    (support) => this.loadFormInfo(support),
                    (error: NtError) =>
                        this.notificationService.sendNotification(
                            'danger',
                            error.error,
                            'No se encontro informacion'
                        )
                );
            }
        });
    }

    public loadFormInfo(support: SupportRequest) {
        console.log('Support:', support);
        Object.keys(this.supportForm.controls).forEach((key) =>
            this.supportForm.controls[key].setValue(
                this.supportForm[key] != undefined && support[key] != null
                    ? support[key]
                    : ''
            )
        );
        console.log('data', this.supportForm.value);
    }

    public onSubmit() {}

    public fileDataUploadListener(event: any): void {
        let reader = new FileReader();
        this.dataFile = new ResourceFile();
        if (event.target.files && event.target.files.length > 0) {
            let file = event.target.files[0];
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
                'El archivo se cargo correctamente'
            );
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error cargando archivo'
            );
        }
        this.loading = false;
    }

    public downloadFile() {
        console.log('downlaod file');
    }

    get contactPhone() {
        return this.supportForm.get('contactPhone')!;
    }
    get contactName() {
        return this.supportForm.get('contactName')!;
    }
    get problem() {
        return this.supportForm.get('problem')!;
    }
    get notes() {
        return this.supportForm.get('notes')!;
    }
    get solution() {
        return this.supportForm.get('solution')!;
    }
    get product() {
        return this.supportForm.get('product')!;
    }
    get contactEmail() {
        return this.supportForm.get('product')!;
    }
    get agent() {
        return this.supportForm.get('agent')!;
    }
    get dueDate() {
        return this.supportForm.get('agent')!;
    }
}
