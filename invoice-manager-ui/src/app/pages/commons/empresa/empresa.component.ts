import { Component, OnInit, TemplateRef } from '@angular/core';
import { Empresa } from '../../../models/empresa';
import { CatalogsData } from '../../../@core/data/catalogs-data';
import { CompaniesData } from '../../../@core/data/companies-data';
import { ZipCodeInfo } from '../../../models/zip-code-info';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { Catalogo } from '../../../models/catalogos/catalogo';
import { CompaniesValidatorService } from '../../../@core/util-services/companies-validator.service';
import { FilesData } from '../../../@core/data/files-data';
import { CuentasData } from '../../../@core/data/cuentas-data';
import { Cuenta } from '../../../models/cuenta';
import {
    NbComponentStatus,
    NbDialogService,
    NbGlobalPhysicalPosition,
    NbToastrService,
} from '@nebular/theme';
import { UsersData } from '../../../@core/data/users-data';
import { ResourceFile } from '../../../models/resource-file';
import { DetalleEmpresa } from '../../../models/detalle-empresa';
import { User } from '../../../@core/models/user';
import { IngresoEmpresa } from '../../../models/ingreso-empresa';
import { DonwloadFileService } from '../../../@core/util-services/download-file-service';

@Component({
    selector: 'ngx-empresa',
    templateUrl: './empresa.component.html',
    styleUrls: ['./empresa.component.scss'],
})
export class EmpresaComponent implements OnInit {
    public companyInfo: Empresa;
    public loading: boolean = false;
    public user: User;

    public formInfo: any = {
        coloniaId: '*',
        logoFileName: '',
        fileDataName: '',
        doctType: '*',
        showCiec: false,
        showFiel: false,
    };
    public coloniaId: number = 0;
    public colonias = [];
    public paises = ['México'];
    public module: string = 'operaciones';
    public isAdministrator: boolean = false;

    public years: string[] = [];
    public girosCat: Catalogo[] = [];
    public banksCat: Catalogo[] = [];

    public legalDocuments: ResourceFile[] = [];
    public contableDocuments: ResourceFile[] = [];

    public observaciones: DetalleEmpresa[] = [];
    public pendientes: DetalleEmpresa[] = [];
    public accionistas: DetalleEmpresa[] = [];
    public apoderados: DetalleEmpresa[] = [];
    public cuentas: Cuenta[] = [];
    public ingresos: IngresoEmpresa[] = [];

    public logo: ResourceFile;

    public dataFile: ResourceFile = new ResourceFile();

    public CONTABLE_FILES = [
        'CSD-CERT',
        'CSD-KEY',
        'FIEL-CERT',
        'FIEL-KEY',
        'REGISTRO_PATRONAL',
        'REPSE',
    ];

    constructor(
        private dialogService: NbDialogService,
        private router: Router,
        private route: ActivatedRoute,
        private sanitizer: DomSanitizer,
        private toastrService: NbToastrService,
        private downloadService: DonwloadFileService,
        private catalogsService: CatalogsData,
        private empresaService: CompaniesData,
        private userService: UsersData,
        private resourcesService: FilesData,
        private accountsService: CuentasData,
        private companiesValidatorService: CompaniesValidatorService
    ) {}

    ngOnInit() {
        this.module = this.router.url.split('/')[2];

        this.companyInfo = new Empresa();
        this.companyInfo.regimenFiscal = '*';
        this.companyInfo.giro = '*';
        this.companyInfo.tipo = '*';
        this.companyInfo.pais = 'México';

        this.calculateYears();
        this.userService.getUserInfo().then(
            (user) => {
                this.user = user;
                this.isAdministrator =
                    user.roles.find((u) => u.role == 'ADMINISTRADOR') !=
                    undefined;
            },
            (error) => {
                let msg =
                    error.error.message ||
                    `${error.statusText} : ${error.message}`;
                this.showToast('danger', 'Error', msg, true);
            }
        );

        this.catalogsService
            .getAllGiros()
            .then(
                (giros: Catalogo[]) => (this.girosCat = giros),
                (error: HttpErrorResponse) => {
                    let msg =
                        error.error.message ||
                        `${error.statusText} : ${error.message}`;
                    this.showToast('danger', 'Error', msg, true);
                }
            )
            .then(() =>
                this.route.paramMap.subscribe((route) => {
                    const rfc = route.get('rfc');
                    if (rfc !== '*') {
                        this.loadCompanyInfo(rfc);
                    }
                })
            );
    }

    public async loadCompanyInfo(rfc: string): Promise<void> {
        this.loading = true;
        try {
            this.companyInfo = await this.empresaService
                .getCompanyByRFC(rfc)
                .toPromise();

            // UPDATING TIME INFO

            this.companyInfo.expiracionCertificado =
                this.companyInfo.expiracionCertificado === undefined
                    ? new Date()
                    : new Date(`${this.companyInfo.expiracionCertificado}`);

            // recovering ZIPCODE INFO
            let cpInfo: ZipCodeInfo = await this.catalogsService.getZipCodeInfo(
                this.companyInfo.cp
            );

            this.colonias = cpInfo.colonias;
            let index = 0;
            cpInfo.colonias.forEach((element) => {
                if (cpInfo.colonias[index] === this.companyInfo.colonia) {
                    this.formInfo.coloniaId = index;
                }
                index++;
            });

            this.observaciones =
                this.companyInfo.detalles.filter(
                    (d) => d.tipo === 'OBSERVACION'
                ) || [];
            this.pendientes =
                this.companyInfo.detalles.filter(
                    (d) => d.tipo === 'PENDIENTE'
                ) || [];
            this.accionistas =
                this.companyInfo.detalles.filter(
                    (d) => d.tipo === 'ACCIONISTA'
                ) || [];
            this.apoderados =
                this.companyInfo.detalles.filter(
                    (d) => d.tipo === 'APODERADO'
                ) || [];
            this.ingresos = this.companyInfo.ingresos;
            this.cuentas = this.companyInfo.cuentas;

            this.loadDocuments(rfc);
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    private async loadDocuments(rfc: string) {
        console.log('Loading documents');
        const documents = await this.resourcesService
            .getResourcesByTypeAndReference('EMPRESAS', rfc)
            .toPromise();

        this.legalDocuments = documents.filter(
            (d) =>
                this.CONTABLE_FILES.find((c) => c == d.tipoArchivo) == undefined
        );
        this.contableDocuments = documents.filter(
            (d) =>
                this.CONTABLE_FILES.find((c) => c == d.tipoArchivo) != undefined
        );

        if (this.legalDocuments.find((d) => d.tipoArchivo === 'LOGO')) {
            // only logo needs to be loaded from backend
            this.resourcesService
                .getResourceFile(rfc, 'EMPRESAS', 'LOGO')
                .subscribe(
                    (logo) => (this.logo = logo),
                    (error) => {
                        let msg =
                            error.error.message ||
                            `${error.statusText} : ${error.message}`;
                        this.showToast('danger', 'Error', msg, true);
                    }
                );
        }
    }

    public sanitize(file: ResourceFile) {
        const url = `data:${file.formato}base64,${file.data}`;
        return this.sanitizer.bypassSecurityTrustUrl(url);
    }

    public toogleCiec() {
        this.formInfo.showCiec = !this.formInfo.showCiec;
    }

    public toogleFiel() {
        this.formInfo.showFiel = !this.formInfo.showFiel;
    }

    public zipCodeInfo(zipcode: String) {
        let zc = new String(zipcode);
        if (zc.length > 4 && zc.length < 6) {
            this.colonias = [];
            this.catalogsService.getZipCodeInfo(zipcode).then(
                (data: ZipCodeInfo) => {
                    this.colonias = data.colonias;
                    this.companyInfo.estado = data.estado;
                    this.companyInfo.municipio = data.municipio;
                    this.companyInfo.colonia = data.colonias[0];
                },
                (error: HttpErrorResponse) => console.error(error)
            );
        }
    }

    public onLocation(index: string) {
        this.companyInfo.colonia = this.colonias[index];
    }

    public onRegimenFiscalSelected(regimen: string) {
        this.companyInfo.regimenFiscal = regimen;
    }

    public onGiroSelection(giro: string) {
        this.companyInfo.giro = giro;
    }

    public onLineaSelected(linea: string) {
        this.companyInfo.tipo = linea;
    }

    public onOperativaSelected(operativa: boolean) {
        this.companyInfo.operativa = operativa;
    }

    public logoUploadListener(event: any): void {
        const reader = new FileReader();
        this.logo = new ResourceFile();
        this.loading = true;
        if (event.target.files && event.target.files.length > 0) {
            const file = event.target.files[0];
            if (file.size > 200000) {
                this.showToast(
                    'warning',
                    'Error',
                    'La imagen del logo es demasiado grande',
                    true
                );
            } else {
                reader.readAsDataURL(file);
                reader.onload = () => {
                    const filename = file.name as string;

                    this.formInfo.logoFileName = filename;
                    this.logo.data = reader.result.toString();
                    this.logo.tipoRecurso = 'EMPRESAS';
                    this.logo.referencia = this.companyInfo.rfc;
                    this.logo.tipoArchivo = 'LOGO';
                    this.logo.extension = filename.substring(
                        filename.lastIndexOf('.'),
                        filename.length
                    );
                    this.resourcesService
                        .insertResourceFile(this.logo)
                        .subscribe(
                            () => {
                                this.showToast(
                                    'info',
                                    'Exito!',
                                    'El logo se cargo correctamente'
                                );
                                this.loading = false;
                                this.loadCompanyInfo(this.companyInfo.rfc);
                            },
                            (error) => {
                                console.error(error);
                                this.loading = false;
                                let msg =
                                    error.error.message ||
                                    `${error.statusText} : ${error.message}`;
                                this.showToast('danger', 'Error', msg, true);
                            }
                        );
                };
                reader.onerror = (error) => {
                    this.loading = false;
                    this.showToast(
                        'danger',
                        'Error',
                        'Error parsing image file',
                        true
                    );
                    console.error(error);
                };
            }
        }
    }

    private async upsertDatafile(
        tipoRecurso: string,
        tipoArchivo: string,
        referencia: string
    ) {
        try {
            this.dataFile.tipoRecurso = tipoRecurso;
            this.dataFile.referencia = referencia;
            this.dataFile.tipoArchivo = tipoArchivo;
            await this.resourcesService
                .insertResourceFile(this.dataFile)
                .toPromise();
            this.formInfo.fileDataName = '';
            this.formInfo.doctType = '*';
            this.dataFile = new ResourceFile();
            this.showToast(
                'info',
                'Exito!',
                'El archivo se cargo correctamente'
            );
        } catch (error) {
            console.error(error);
            this.formInfo.fileDataName = '';
            this.formInfo.doctType = '*';
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
    }

    private sleep(duration) {
        return new Promise<void>((resolve) => {
            setTimeout(() => {
                resolve();
            }, duration * 1000);
        });
    }

    public async fileDocumentUpload(): Promise<void> {
        try {
            const rfc = this.companyInfo.rfc;
            this.loading = true;
            this.upsertDatafile('EMPRESAS', this.formInfo.doctType, rfc);

            this.sleep(1).then(() => this.loadDocuments(rfc));
        } catch (error) {
            console.error(error);
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
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
                this.showToast(
                    'danger',
                    'Error',
                    'Error cargando el archivo',
                    true
                );
            };
        }
    }

    public async insertNewCompany() {
        try {
            let errorMessages = this.companiesValidatorService.validarEmpresa(
                this.companyInfo
            );
            if (errorMessages.length === 0) {
                this.companyInfo.creador = this.user.email;
                this.companyInfo = await this.empresaService
                    .insertNewCompany(this.companyInfo)
                    .toPromise();
                this.showToast(
                    'info',
                    'Exito!',
                    'La empresa ha sido creada correctamente'
                );
            } else {
                for (const msg of errorMessages) {
                    this.showToast('warning', 'Falta información', msg, true);
                }
            }
        } catch (error) {
            console.error(error);
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
    }

    public async updateCompany() {
        try {
            await this.empresaService
                .updateCompany(this.companyInfo.rfc, this.companyInfo)
                .toPromise();
            this.showToast(
                'info',
                'Exito!',
                'La empresa ha sido actualizada correctamente'
            );
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
    }

    public async inactivateCompany() {
        const company = { ...this.companyInfo };
        company.activo = false;
        company.estatus = 'INACTIVA';
        this.loading = true;
        try {
            this.companyInfo = await this.empresaService
                .updateCompany(this.companyInfo.rfc, company)
                .toPromise();
            this.showToast(
                'info',
                'Exito!',
                'La empresa ha sido desactivada satisfactoriamente'
            );
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async activateCompany() {
        try {
            this.loading = true;
            const cert = this.contableDocuments.find(
                (d) => d.tipoArchivo === 'CSD-CERT'
            );
            const key = this.contableDocuments.find(
                (d) => d.tipoArchivo === 'CSD-KEY'
            );
            const logo = this.legalDocuments.find(
                (d) => d.tipoArchivo === 'LOGO'
            );

            if (cert == undefined) {
                this.showToast(
                    'warning',
                    'Falta certificado',
                    'Es necesario la carga del certificado para activar la empresa',
                    true
                );
            }
            if (key == undefined) {
                this.showToast(
                    'warning',
                    'Falta llave',
                    'Es necesario la carga de la llave para activar la empresa',
                    true
                );
            }

            if (logo == undefined) {
                this.showToast(
                    'warning',
                    'Falta logo empresa',
                    'Es necesario la carga del logo para activar la empresa',
                    true
                );
            }

            if (
                this.companyInfo.noCertificado == undefined ||
                this.companyInfo.noCertificado.length == 0
            ) {
                this.showToast(
                    'warning',
                    'Falta no Certificado',
                    'Es necesario dar de alta el no de certificado para activar la empresa',
                    true
                );
            }

            if (cert && key && logo && this.companyInfo.noCertificado) {
                const company = { ...this.companyInfo };
                company.activo = true;
                company.estatus = 'ACTIVA';
                this.companyInfo = await this.empresaService
                    .updateCompany(this.companyInfo.rfc, company)
                    .toPromise();
                this.showToast(
                    'info',
                    'Exito!',
                    'La empresa ha sido activada satisfactoriamente'
                );
            }
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async openAnualDataDialog(dialog: TemplateRef<any>) {
        this.dataFile = new ResourceFile();
        this.formInfo.fileDataName = '';
        const anualData = new IngresoEmpresa();
        anualData.rfc = this.companyInfo.rfc;
        anualData.tipoDato = 'INGRESO';
        anualData.anio = new Date().getFullYear().toString();
        anualData.creador = this.user.email;
        try {
            let result = await this.dialogService
                .open(dialog, { context: anualData })
                .onClose.toPromise();
            if (result) {
                this.loading = true;
                if (this.dataFile.data != undefined) {
                    this.upsertDatafile(
                        'EMPRESAS',
                        `${result.tipoDato}`,
                        `${this.companyInfo.rfc}-${result.tipoDato}-${result.anio}`
                    );
                    result.link = `/recursos/EMPRESAS/referencias/${this.companyInfo.rfc}-${result.tipoDato}-${result.anio}/files/${result.tipoDato}`;
                }
                await this.empresaService
                    .insertCompanyIncome(result)
                    .toPromise();
                this.showToast(
                    'info',
                    'Dato anual creado!',
                    `El dato se cargo exitosamente`
                );
                this.loadCompanyInfo(this.companyInfo.rfc);
            }
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async deleteAnualData(id: number) {
        this.loading = true;
        try {
            await this.empresaService
                .deleteCompanyIncome(this.companyInfo.rfc, id)
                .toPromise();
            this.showToast(
                'info',
                'Dato borrado!',
                `El dato se ha borrado exitosamente`
            );
            this.loadCompanyInfo(this.companyInfo.rfc);
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async openAccountDialog(rfc: string, cuenta: number) {
        this.router.navigate([
            `./pages/${this.module}/cuenta-bancaria/${rfc}/${cuenta}`,
        ]);
    }

    public async deleteAccount(id: number) {
        this.loading = true;
        try {
            await this.accountsService.deleteCuenta(id.toString()).toPromise();
            this.showToast(
                'info',
                'Cuenta borrada',
                'La cuenta se ha borrado exitosamente'
            );
            this.loadCompanyInfo(this.companyInfo.rfc);
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async openDetallesDialog(
        dialog: TemplateRef<any>,
        detail: DetalleEmpresa,
        type?: string
    ) {
        this.dataFile = new ResourceFile();
        this.formInfo.fileDataName = '';

        const detalle =
            detail ||
            new DetalleEmpresa(
                this.companyInfo.rfc,
                this.module,
                this.user.email,
                type
            );

        try {
            let result = await this.dialogService
                .open(dialog, { context: detalle })
                .onClose.toPromise();

            if (result) {
                this.loading = true;
                if (result.id) {
                    // update detail
                    const detail = await this.empresaService
                        .updateCompanyDetail(detalle)
                        .toPromise();
                    if (this.dataFile.data != undefined) {
                        this.upsertDatafile(
                            'EMPRESAS',
                            detail.tipo,
                            `${this.companyInfo.rfc}-${detail.tipo}-${detail.id}`
                        );
                    }
                    this.showToast(
                        'info',
                        'Detalle actualizado',
                        `${detail.tipo} correctamente actualizado`
                    );
                    this.loadCompanyInfo(this.companyInfo.rfc);
                } else {
                    const detail = await this.empresaService
                        .insertCompanyDetail(detalle)
                        .toPromise();
                    if (this.dataFile.data != undefined) {
                        this.upsertDatafile(
                            'EMPRESAS',
                            detail.tipo,
                            `${this.companyInfo.rfc}-${detail.tipo}-${detail.id}`
                        );
                    }
                    this.showToast(
                        'info',
                        'Detalle creado',
                        `${detail.tipo} correctamente creado`
                    );
                    this.loadCompanyInfo(this.companyInfo.rfc);
                }
            }
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public async deleteDetail(id: number) {
        this.loading = true;
        try {
            await this.empresaService.deleteCompanyDetail(id).toPromise();
            this.showToast(
                'info',
                'Detalle borrado',
                'El detalle se ha borrado exitosamente'
            );
            this.loadCompanyInfo(this.companyInfo.rfc);
        } catch (error) {
            let msg =
                error.error.message || `${error.statusText} : ${error.message}`;
            this.showToast('danger', 'Error', msg, true);
        }
        this.loading = false;
    }

    public downloadDocumentData(dato: IngresoEmpresa) {
        this.downloadService.dowloadResourceFile(
            dato.link,
            `${dato.rfc}_${dato.tipoDato}_${dato.anio}`
        );
    }
    public downloadDocumentDetail(detail: DetalleEmpresa) {
        this.downloadService.dowloadResourceFile(
            `/recursos/EMPRESAS/referencias/${this.companyInfo.rfc}-${detail.tipo}-${detail.id}/files/${detail.tipo}`,
            `${this.companyInfo.rfc}_${detail.tipo}`
        );
    }

    public downloadFile(file: ResourceFile) {
        const path: string = `/recursos/${file.tipoRecurso}/referencias/${file.referencia}/files/${file.tipoArchivo}`;
        this.downloadService.dowloadResourceFile(
            path,
            `${file.referencia}_${file.tipoArchivo}`
        );
    }

    private calculateYears() {
        const start = new Date().getFullYear() - 10;
        for (let index = start; index < start + 20; index++) {
            this.years.push(index.toString());
        }
    }

    private showToast(
        type: NbComponentStatus,
        title: string,
        body: string,
        clickdestroy?: boolean
    ) {
        const config = {
            status: type,
            destroyByClick: clickdestroy || false,
            duration: 8000,
            hasIcon: true,
            position: NbGlobalPhysicalPosition.TOP_RIGHT,
            preventDuplicates: true,
        };
        const titleContent = title ? `${title}` : 'xxxx';

        this.toastrService.show(body, titleContent, config);
    }
}
