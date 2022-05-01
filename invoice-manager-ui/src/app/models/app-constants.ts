import { NbGlobalPhysicalPosition } from "@nebular/theme";

export class AppConstants {
  static readonly INFO_TOAST = {
    status: "info",
    destroyByClick: true,
    duration: 8000,
    hasIcon: true,
    position: NbGlobalPhysicalPosition.TOP_RIGHT,
    preventDuplicates: true,
  };

  static readonly WARN_TOAST = {
    status: "warning",
    destroyByClick: true,
    duration: 8000,
    hasIcon: true,
    position: NbGlobalPhysicalPosition.TOP_RIGHT,
    preventDuplicates: true,
  };

  static readonly DANGER_TOAST = {
    status: "danger",
    destroyByClick: true,
    duration: 8000,
    hasIcon: true,
    position: NbGlobalPhysicalPosition.TOP_RIGHT,
    preventDuplicates: true,
  };
}
