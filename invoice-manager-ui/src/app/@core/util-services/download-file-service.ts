import { Injectable } from '@angular/core';
import { ResourceFile } from '../../models/resource-file';
import { FilesData } from '../data/files-data';
import { InvoicesData } from '../data/invoices-data';

@Injectable({
  providedIn: 'root',
})
export class DonwloadFileService {


  constructor(private resourcesService: FilesData) { }


  public async dowloadResourceFile(url: string,filename: string){
    const file: ResourceFile = await this.resourcesService.getResourceFileFromUrl(url).toPromise();
    this.downloadFile(file.data,`${filename}${file.extension}`,file.formato);
  }

  public downloadFile(data: any, filename: string, fileType: string) {
    console.log(`Downloading ${filename} ...`)
    if (data == null || data == undefined || data.length < 1) {
      console.error("Donwload service can't generate report from empty or null data.");
      alert("No se encontro información, imposible generar reporte.");
      return;
    } else {
      const byteString = window.atob(data);
      const arrayBuffer = new ArrayBuffer(byteString.length);
      const int8Array = new Uint8Array(arrayBuffer);
      for (let i = 0; i < byteString.length; i++) {
        int8Array[i] = byteString.charCodeAt(i);
      }
      const blob = new Blob([int8Array], { type: fileType });
      if (navigator.msSaveBlob) {
        navigator.msSaveBlob(blob, filename.replace(/ /g, "_"));
      } else {
        let link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.setAttribute('visibility', 'hidden');
        link.download = filename.replace(/ /g, "_");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  }



}