import { Component, OnInit } from '@angular/core';
import { Client } from '../../../models/client';
import { ClientsData } from '../../../@core/data/clients-data';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { ActivatedRoute, Router } from '@angular/router';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { ClientsValidatorService } from '../../../@core/util-services/clients-validator.service';
import { ResourceFile } from '../../../models/resource-file';
import { FilesData } from '../../../@core/data/files-data';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';
import { NtError } from '../../../@core/models/nt-error';
import { NotificationsService } from '../../../@core/util-services/notifications.service';

import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AppConstants } from '../../../models/app-constants';
import {
    AsyncValidatorFn,
    AbstractControl,
    ValidationErrors,
} from '@angular/forms';

@Component({
    selector: 'ngx-cliente',
    templateUrl: './cliente.component.html',
    styleUrls: ['./cliente.component.scss'],
})
export class ClienteComponent implements OnInit {
    public module: string = 'promotor';
    public clientInfo: Client;
    public formInfo: any = {
        id: '',
        rfc: '',
        coloniaId: '*',
        fileDataName: '',
    };
    public coloniaId: number = 0;
    public colonias = [];
    public regimenes = [];
    public loading: boolean = false;

    private dataFile: ResourceFile;
    public clienteForm = new FormGroup({
        rfc: new FormControl('', {
            validators: [
                Validators.required,
                Validators.minLength(AppConstants.RFC_MIN_LENGTH),
                Validators.maxLength(AppConstants.RFC_MAX_LENGTH),
                Validators.pattern(AppConstants.RFC_PATTERN),
            ],
            asyncValidators: [
                this.doesUserHasThisRFCAssociatedValidator(this.clientService),
            ],
            updateOn: 'blur',
        }),
        razonSocial: new FormControl('', [
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(300),
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        pais: new FormControl('MEX', [
            Validators.pattern(AppConstants.COUNTRY_CODE_PATTERN),
        ]),
        cp: new FormControl('', [
            Validators.required,
            Validators.pattern(AppConstants.ZIP_CODE_PATTERN),
        ]),
        localidad: new FormControl('', [
            Validators.maxLength(200),
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        municipio: new FormControl('', [
            Validators.maxLength(150),
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        estado: new FormControl('', [
            Validators.maxLength(45),
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        calle: new FormControl('', [
            Validators.maxLength(200),
            Validators.pattern(AppConstants.GENERIC_TEXT_PATTERN),
        ]),
        noExterior: new FormControl('', [Validators.maxLength(45)]),
        noInterior: new FormControl('', [Validators.maxLength(45)]),
        regimenFiscal: new FormControl('*', [
            Validators.required,
            Validators.pattern(AppConstants.FISCAL_REGIMEN_PATTERN),
        ]),
        correoContacto: new FormControl('', [
            Validators.required,
            Validators.maxLength(100),
            Validators.email,
        ]),
        porcentajeCliente: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
        ]),
        porcentajeContacto: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
        ]),
        porcentajeDespacho: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
        ]),
        porcentajePromotor: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
        ]),
        notas: new FormControl('', []),
    });

    constructor(
        private resourcesService: FilesData,
        private downloadService: DonwloadFileService,
        private clientService: ClientsData,
        private clientValidatorService: ClientsValidatorService,
        private notificationService: NotificationsService,
        private catalogsService: CatalogsData,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.catalogsService
            .getAllRegimenFiscal()
            .then((reg) => (this.regimenes = reg));
    }

    ngOnInit(): void {
        this.module = this.router.url.split('/')[2];
        this.clientInfo = new Client();
        this.clientInfo.pais = 'MEX';
        /** recovering folio info**/
        this.route.paramMap.subscribe((route) => {
            const id = route.get('id');
            if (id !== '*') {
                this.loadClientInfo(+id);
            } else {
                this.clienteForm.patchValue(new Client());
            }
        });
    }

    public async loadClientInfo(id: number) {
        this.loading = true;
        try {
            this.clientInfo = await this.clientService
                .getClientById(id)
                .toPromise();
            this.formInfo.id = this.clientInfo.id;
            this.formInfo.rfc = this.clientInfo.rfc;
            this.formInfo.coloniaId = 'other';
            this.clienteForm.patchValue(this.clientInfo);
            this.colonias = (
                await this.catalogsService.getZipCodeInfo(this.clientInfo.cp)
            ).colonias;
            this.colonias
                .filter((colonia) => colonia === this.clientInfo.localidad)
                .forEach((colonia) => (this.formInfo.coloniaId = colonia));
        } catch (error) {
            this.loading = false;
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
        }
        this.loading = false;
        this.dataFile = await this.resourcesService
            .getResourceFile(
                this.clientInfo.id.toString(),
                'CLIENTES',
                'DOCUMENTO'
            )
            .toPromise();
    }

    public doesUserHasThisRFCAssociatedValidator(
        service: ClientsData
    ): AsyncValidatorFn {
        return (control: AbstractControl): Promise<ValidationErrors> => {
            let promotor = sessionStorage.getItem('email');
            let rfc = control.value;
            return service
                .getClientsByPromotorAndRfc(promotor, rfc)
                .toPromise()
                .then((record) => {
                    return record && !this.clientInfo.id
                        ? { rfcExist: true }
                        : null;
                });
        };
    }

    public async updateClient() {
        const client: Client = { ...this.clienteForm.value };
        this.loading = true;
        try {
            const errors: string[] =
                this.clientValidatorService.validarCliente(client);
            if (errors.length > 0) {
                for (const err of errors) {
                    this.notificationService.sendNotification(
                        'warning',
                        err,
                        'Falta información'
                    );
                }
                this.loading = false;
                return;
            }
            client.id = this.clientInfo.id;
            client.correoPromotor = this.clientInfo.correoPromotor;
            client.fechaCreacion = this.clientInfo.fechaCreacion;
            this.clientInfo = await this.clientService
                .updateClient(client)
                .toPromise();
            this.notificationService.sendNotification(
                'info',
                'La información del cliente fue actualizada satisfactoriamente'
            );
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
        }
        this.loading = false;
    }

    public async insertClient() {
        const client: Client = { ...this.clienteForm.value };
        this.loading = true;
        try {
            const errors: string[] =
                this.clientValidatorService.validarCliente(client);
            if (errors.length > 0) {
                for (const err of errors) {
                    this.notificationService.sendNotification(
                        'warning',
                        err,
                        'Falta información'
                    );
                }
                this.loading = false;
                return;
            }
            client.correoPromotor = sessionStorage.getItem('email');
            this.clientInfo = await this.clientService
                .insertNewClient(client)
                .toPromise();

            if (this.dataFile !== undefined) {
                await this.uploadFile();
            } else {
                this.notificationService.sendNotification(
                    'warning',
                    'Falta comprobante situación fiscal',
                    'Sin comprobante de situación fiscal del cliente, este es un documento requerido en CFDI 4.0'
                );
            }
            this.notificationService.sendNotification(
                'info',
                'Cliente creado correctamente'
            );
            this.loading = false;

            const url = this.router.url;
            const redirectUrl = `.${url.substring(0, url.lastIndexOf('/'))}/${
                this.clientInfo.id
            }`;
            this.router.navigate([redirectUrl]);
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
            this.loading = false;
        }
    }

    public onLocation(colonia: string) {
        console.log('Form', this.clienteForm);
        if (colonia !== 'other' && colonia !== '*') {
            this.clienteForm.controls['localidad'].setValue(colonia);
        }
    }

    public zipCodeInfo(zc: string) {
        if (zc.length === 5) {
            this.colonias = [];
            this.catalogsService.getZipCodeInfo(zc).then(
                (data: ZipCodeInfo) => {
                    this.colonias = data.colonias;
                    if (this.colonias.length < 1) {
                        this.formInfo.coloniaId = 'other';
                        this.clienteForm.controls['estado'].setValue('');
                        this.clienteForm.controls['municipio'].setValue('');
                        this.clienteForm.controls['localidad'].setValue('');
                        this.notificationService.sendNotification(
                            'warning',
                            `No se ha encontrado información pata el codigo postal ${zc}`
                        );
                    } else {
                        this.formInfo.coloniaId = data.colonias[0];
                        this.clienteForm.controls['estado'].setValue(
                            data.estado
                        );
                        this.clienteForm.controls['municipio'].setValue(
                            data.municipio
                        );
                        this.clienteForm.controls['localidad'].setValue(
                            this.formInfo.coloniaId
                        );
                    }
                },
                (error: NtError) => {
                    this.formInfo.coloniaId = 'other';
                    this.clienteForm.controls['estado'].setValue(
                        this.clientInfo['estado'] || ''
                    );
                    this.clienteForm.controls['municipio'].setValue(
                        this.clientInfo['municipio'] || ''
                    );
                    this.clienteForm.controls['localidad'].setValue(
                        this.clientInfo['localidad'] || ''
                    );
                    this.notificationService.sendNotification(
                        'warning',
                        error?.message,
                        'Error'
                    );
                }
            );
        }
    }

    public async toggleOn() {
        const client: Client = { ...this.clienteForm.value };
        client.activo = true;
        this.loading = true;
        try {
            client.id = this.clientInfo.id;
            client.correoPromotor = this.clientInfo.correoPromotor;
            client.fechaCreacion = this.clientInfo.fechaCreacion;
            this.clientInfo = await this.clientService
                .updateClient(client)
                .toPromise();
            this.notificationService.sendNotification(
                'info',
                'Cliente activado exitosamente'
            );
        } catch (error) {
            this.notificationService.sendNotification(
                'danger',
                error?.message,
                'Error'
            );
        }
        this.loading = false;
    }

    public async toggleOff() {
        const client: Client = { ...this.clienteForm.value };
        client.activo = false;
        this.loading = true;
        try {
            client.id = this.clientInfo.id;
            client.correoPromotor = this.clientInfo.correoPromotor;
            client.fechaCreacion = this.clientInfo.fechaCreacion;
            this.clientInfo = await this.clientService
                .updateClient(client)
                .toPromise();
            this.notificationService.sendNotification(
                'info',
                'Cliente desactivado exitosamente'
            );
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.notificationService.sendNotification('danger', 'Error', msg);
        }
        this.loading = false;
    }

    public fileDataUploadListener(event: any): void {
        let reader = new FileReader();
        this.dataFile = new ResourceFile();
        if (event.target.files && event.target.files.length > 0) {
            let file = event.target.files[0];
            reader.readAsDataURL(file);
            reader.onload = () => {
                this.formInfo.fileDataName = file.name;
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

    public async uploadFile(): Promise<void> {
        try {
            this.loading = true;
            this.dataFile.tipoRecurso = 'CLIENTES';
            this.dataFile.referencia = this.clientInfo.id.toString();
            this.dataFile.tipoArchivo = 'DOCUMENTO';
            await this.resourcesService
                .insertResourceFile(this.dataFile)
                .toPromise();
            this.notificationService.sendNotification(
                'info',
                'El archivo se cargo correctamente'
            );
            this.formInfo.fileDataName = '';
            this.formInfo.doctType = '*';
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
        const path: string = `/recursos/CLIENTES/referencias/${this.clientInfo.id}/files/DOCUMENTO`;
        this.downloadService.dowloadResourceFile(
            path,
            `DocumentoRelacionado_${this.clientInfo.rfc}`
        );
    }

    get rfc() {
        return this.clienteForm.get('rfc')!;
    }
    get razonSocial() {
        return this.clienteForm.get('razonSocial')!;
    }
    get pais() {
        return this.clienteForm.get('pais')!;
    }
    get cp() {
        return this.clienteForm.get('cp')!;
    }
    get localidad() {
        return this.clienteForm.get('localidad')!;
    }
    get municipio() {
        return this.clienteForm.get('municipio')!;
    }
    get estado() {
        return this.clienteForm.get('estado')!;
    }
    get calle() {
        return this.clienteForm.get('calle')!;
    }
    get noExterior() {
        return this.clienteForm.get('noExterior')!;
    }
    get noInterior() {
        return this.clienteForm.get('noInterior')!;
    }
    get regimenFiscal() {
        return this.clienteForm.get('regimenFiscal')!;
    }
    get correoContacto() {
        return this.clienteForm.get('correoContacto')!;
    }
    get porcentajeCliente() {
        return this.clienteForm.get('porcentajeCliente')!;
    }
    get porcentajeContacto() {
        return this.clienteForm.get('porcentajeContacto')!;
    }
    get porcentajeDespacho() {
        return this.clienteForm.get('porcentajeDespacho')!;
    }
    get porcentajePromotor() {
        return this.clienteForm.get('porcentajePromotor')!;
    }
    get notas() {
        return this.clienteForm.get('notas')!;
    }
}
