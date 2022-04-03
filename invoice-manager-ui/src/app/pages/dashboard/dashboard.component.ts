import {Component, OnInit} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { map } from 'rxjs/operators';
import { CompaniesData } from '../../@core/data/companies-data';
import { FilesData } from '../../@core/data/files-data';
import { GenericPage } from '../../models/generic-page';

interface CardSettings {
  title: string;
  imageSrc: string;
  description: string;
  linkUrl:string;
}

@Component({
  selector: 'ngx-dashboard',
  styleUrls: ['./dashboard.component.scss'],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent  implements OnInit{

 constructor(private companiesService:CompaniesData,
  private sanitizer: DomSanitizer,
  private resourcesService: FilesData){

 }

 public cards:CardSettings[]= []

  ngOnInit(): void {
    this.companiesService.getCompanies({activo: true, linea: 'A', size:12})
    .pipe(
      map((page:GenericPage<any>)=>{
        let result = page.content.map(e=>Object.assign({}, {title:e.NOMBRE_CORTO,imageSrc:`/api/empresas/${e.RFC}/logo`,description:e.GIRO,linkUrl:e.PAGINA_WEB}));
        return result;
      })
    ).subscribe(result =>this.cards = result)
  }

  public goToLink(link:string){
    console.log('Redirecting to :', link)
    window.open(link, "_blank");
  }

  /*
  public async getlogoFromRFC(rfc:string){
    let file: ResourceFile = await this.resourcesService.getResourceFile(rfc, 'EMPRESAS', 'LOGO').toPromise();
    const url = `data:${file.formato}base64,${file.data}`;
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }*/
}
