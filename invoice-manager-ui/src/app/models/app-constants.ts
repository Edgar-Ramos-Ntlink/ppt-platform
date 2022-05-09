import { NbGlobalPhysicalPosition } from '@nebular/theme';

export class AppConstants {
    static readonly TOAST_CONFIG = {
        destroyByClick: true,
        duration: 8000,
        hasIcon: true,
        position: NbGlobalPhysicalPosition.TOP_RIGHT,
        preventDuplicates: true,
    };
}
