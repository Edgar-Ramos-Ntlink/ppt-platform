import { Component, OnInit } from '@angular/core';
import { UsersData } from '../@core/data/users-data';
import { User } from '../@core/models/user';

@Component({
  selector: 'ngx-pages',
  styleUrls: ['pages.component.scss'],
  template: `
    <ngx-one-column-layout [user]=user>
      <nb-menu [items]="user.menu"></nb-menu>
      <router-outlet></router-outlet>
    </ngx-one-column-layout>
  `,
})
export class PagesComponent implements OnInit {

  constructor(private userService: UsersData ) { }


  public user = new User();

  public ngOnInit() {
    this.userService.getUserInfo().then(user => {this.user = user;});
  }
}
