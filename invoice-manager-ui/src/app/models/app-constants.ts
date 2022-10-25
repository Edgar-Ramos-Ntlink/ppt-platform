import { NbGlobalPhysicalPosition } from '@nebular/theme';

export class AppConstants {
    static readonly TOAST_CONFIG = {
        destroyByClick: true,
        duration: 8000,
        hasIcon: true,
        position: NbGlobalPhysicalPosition.TOP_RIGHT,
        preventDuplicates: true,
    };

    static readonly RFC_PATTERN = '^[A-Z&amp;Ñ]{3,4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]$';
    static readonly RFC_MAX_LENGTH = 13;
    static readonly RFC_MIN_LENGTH = 12;
    static readonly GENERIC_TEXT_PATTERN = '^[A-Za-z\\d\\sÁÉÍÓÚÑáéíóúñ.,!$%@#£€*?&-_]+$';
    static readonly LETTER_TEXT_PATTERN = '^[A-Za-z\\sÁÉÍÓÚÑáéíóúñ.,-_]+$';
    static readonly NUMERIC_TEXT_PATTERN = '^[\\d\\s.,-_]+$';
    static readonly TAX_REGIMEN_PATTERN = '^[0-9]{3}$';
    static readonly ZIP_CODE_PATTERN ='^[0-9]{5}$';
    static readonly COUNTRY_CODE_PATTERN = '^[A-Z]{3}$';
    static readonly UUID_PATTERN = '^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$';
}
