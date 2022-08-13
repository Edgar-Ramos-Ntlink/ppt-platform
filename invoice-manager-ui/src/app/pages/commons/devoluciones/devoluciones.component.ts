import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { e } from 'mathjs';
import { map } from 'rxjs/operators';
import { ClientsData } from '../../../@core/data/clients-data';
import { UsersData } from '../../../@core/data/users-data';
import { NtError } from '../../../@core/models/nt-error';
import { User } from '../../../@core/models/user';
import { NotificationsService } from '../../../@core/util-services/notifications.service';
import { Client } from '../../../models/client';
import { Devolucion } from '../../../models/devolucion';
import { GenericPage } from '../../../models/generic-page';

@Component({
  selector: 'nt-devoluciones',
  templateUrl: './devoluciones.component.html',
  styleUrls: ['./devoluciones.component.scss']
})
export class DevolucionesComponent implements OnInit {

  public loading: boolean = false;

  public usersCat: User[]=[];
  public clientsCat: Client[]=[];
  public return: Devolucion = new Devolucion();

  constructor(
    private notificationService:NotificationsService,
    private usersService:UsersData,
    private clientsService: ClientsData,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(route => {
      const id = route.get('id');
      if (id !== '*') {
        this.loadReturnInfo(+id);
      } else {
        this.usersService.getUsers(0,1000,{status:'1'})
        .pipe(
          map((p:GenericPage<User>)=> {const users = p.content; users.forEach(u=>u.name=`${u.alias} - ${u.email}`); return users;})
        ).subscribe(users=>this.usersCat= users,(error:NtError)=>this.notificationService.sendNotification('danger',error.message,'Error cargando promotores'))
      }
    });

    this.return.pagos = [
      {razonSocialEmpresa:'ABASTECEDORA GEMCO',rfcEmpresa:'AGE2112158F4',folio:'2002636373766242',banco:'SANTANDER',fechaPago:'2022-10-23',monto:4500.67},
      {razonSocialEmpresa:'ALASTORE MEXICO',rfcEmpresa:'AME140512D80',folio:'2002636675278737',banco:'HSBC',fechaPago:'2022-09-21',monto:9500.67},
      {razonSocialEmpresa:'AXKAN PUBLICIDAD',rfcEmpresa:'APU140519728',folio:'2002636725216217',banco:'AZTECA',fechaPago:'2022-12-20',monto:14500.67},
      {razonSocialEmpresa:'BLAKE INGENIERIA INTEGRAL',rfcEmpresa:'BII180413413',folio:'2002639097363912',banco:'BBVA',fechaPago:'2022-07-05',monto:5500.67},
      {razonSocialEmpresa:'CONSTRUCTORA URBANA APIRO',rfcEmpresa:'CUA210415MC1',folio:'2002630236537281',banco:'SANTANDER',fechaPago:'2022-05-29',monto:47500.67},
      {razonSocialEmpresa:'CORPORATIVO NOVUM PROCESSAR',rfcEmpresa:'CNP101126SL8',folio:'2002638391917178',banco:'BANORTE',fechaPago:'2022-03-10',monto:98000.67},
      {razonSocialEmpresa:'ENVISION ACTION GROUP',rfcEmpresa:'EAG211209FP3',folio:'2000385673821192',banco:'INBURSA',fechaPago:'2022-05-12',monto:3300.33},
    ];

    this.return.total = 237839.44
  }

  public selectPromotor(user:User){
    this.return.promotor = user.email;
    this.clientsService.getClientsByPromotor(user.email)
    .pipe(
      map((clients:Client[])=> {clients.forEach(c=>c.notas=`${c.rfc} - ${c.razonSocial}`); console.log(clients); return clients;})
    ).subscribe(clients=>this.clientsCat = clients,(error:NtError)=>this.notificationService.sendNotification('danger',error.message,'Error cargando clientes'))
  }

  public selectClient(client:Client){
    this.return.rfcCliente = client.rfc;
    this.return.porcentajeContacto = client.porcentajeContacto;
    this.return.montoContacto = (this.return.total*client.porcentajeContacto)/ 116; 
    this.return.procentajeCliente = client.porcentajeCliente;
    this.return.montoCliente = (this.return.total*client.porcentajeCliente)/ 116; 
    this.return.porcentajeDespacho = client.porcentajeDespacho;
    this.return.montoDespacho = (this.return.total*client.porcentajeDespacho)/ 116; 
    this.return.porcentajePromotor = client.porcentajePromotor;
    this.return.montoPromotor = (this.return.total*client.porcentajePromotor)/ 116; 
    console.log('client:',client)
  }

  public loadReturnInfo(id:number){
    console.log('Recovering return info for',id);
  }

}
