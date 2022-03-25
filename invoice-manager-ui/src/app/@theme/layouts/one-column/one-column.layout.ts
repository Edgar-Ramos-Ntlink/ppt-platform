import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';

import { LayoutService } from '../../../@core/utils';
import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { UsersData } from '../../../@core/data/users-data';
import { User } from '../../../models/user';

@Component({
  selector: 'ngx-one-column-layout',
  styleUrls: ['./one-column.layout.scss'],
  template: `
    <nb-layout windowMode>
      <nb-layout-header fixed>
        <div class="header-container">
          <div class="logo-container">
            <a (click)="toggleSidebar()" href="#" class="sidebar-toggle">
              <nb-icon icon="menu-2-outline"></nb-icon>
            </a>
            <a class="logo" href="#" (click)="navigateHome()"><span>NT LINK ADMINISTRACIÓN</span></a>
          </div>
        </div>
    
        <div class="header-container clearfix">
          <!--nb-select [selected]="currentTheme" (selectedChange)="changeTheme($event)" status="primary" class="float-right">
            <nb-option *ngFor="let theme of themes" [value]="theme.value"> {{ theme.name }}</nb-option>
          </nb-select-->
          <button class="btn btn-warning float-right" (click)="logout()"> Salir</button>
          <nb-actions size="small" class="float-right">
            <nb-action class="user-action">
              <nb-user shape="rectangle"
              [name]="user?.name"
              [picture]="user?.urlPicture" title="Usuario">
              </nb-user>
            </nb-action>
          </nb-actions>
        </div>
      </nb-layout-header>

      <nb-sidebar class="menu-sidebar" tag="menu-sidebar" responsive>
        <ng-content select="nb-menu"></ng-content>
      </nb-sidebar>

      <nb-layout-column>
        <ng-content select="router-outlet"></ng-content>
      </nb-layout-column>

      <nb-layout-footer fixed>
        <span class="created-by">
          Desarrolado por <b><a href="http://www.ntlink.com.mx" target="_blank">NT LINK</a></b>  &copy; 2022. Todos los derechos reservados. V 3.6.0
        </span>
      </nb-layout-footer>
    </nb-layout>
  `,
})
export class OneColumnLayoutComponent implements OnInit, OnDestroy {

  @Input() user: User;

  private destroy$: Subject<void> = new Subject<void>();
  userPictureOnly: boolean = false;

  themes = [
    {
      value: 'default',
      name: 'Light',
    },
    {
      value: 'dark',
      name: 'Dark',
    },
    {
      value: 'cosmic',
      name: 'Cosmic',
    },
    {
      value: 'corporate',
      name: 'Corporate',
    },
  ];

  currentTheme = 'default';

  
  constructor(private sidebarService: NbSidebarService,
              private menuService: NbMenuService,
              private themeService: NbThemeService,
              private layoutService: LayoutService,
              private breakpointService: NbMediaBreakpointsService,
              private userService: UsersData,
              private router: Router) {
  }

  ngOnInit() {
    this.currentTheme = this.themeService.currentTheme;

   
    const { xl } = this.breakpointService.getBreakpointsMap();
    this.themeService.onMediaQueryChange()
      .pipe(
        map(([, currentBreakpoint]) => currentBreakpoint.width < xl),
        takeUntil(this.destroy$),
      )
      .subscribe((isLessThanXl: boolean) => this.userPictureOnly = isLessThanXl);

    this.themeService.onThemeChange()
      .pipe(
        map(({ name }) => name),
        takeUntil(this.destroy$),
      )
      .subscribe(themeName => this.currentTheme = themeName);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  changeTheme(themeName: string) {
    this.themeService.changeTheme(themeName);
  }

  toggleSidebar(): boolean {
    this.sidebarService.toggle(true, 'menu-sidebar');
    this.layoutService.changeLayoutSize();

    return false;
  }

  navigateHome() {
    this.menuService.navigateHome();
    return false;
  }

  logout() {
    this.userService.logout().subscribe({
      error(e) { console.error('logout',e) },
      complete() { this.document.location.href = "https://mail.google.com/mail/u/0/?logout&hl=en"}
    });
  }
}
