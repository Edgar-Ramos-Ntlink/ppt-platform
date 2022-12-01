import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
    NbMediaBreakpointsService,
    NbMenuService,
    NbSidebarService,
    NbThemeService,
} from '@nebular/theme';

import { LayoutService } from '../../../@core/utils';
import { filter, map } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { User } from '../../../@core/models/user';
import { AuthService } from '../../../auth/auth.service';
import { select, Store } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { notifications } from '../../theme.selectors';
import { AppNotification } from '../../../@core/models/app-notification';
import { Router } from '@angular/router';

@Component({
    selector: 'ngx-one-column-layout',
    styleUrls: ['./one-column.layout.scss'],
    templateUrl: './one-column.layout.html',
})
export class OneColumnLayoutComponent implements OnInit, OnDestroy {
    @Input() user: User;

    private destroy$: Subject<void> = new Subject<void>();
    public notifications: AppNotification[] = [];
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

    supportItems = [
        { title: 'Solicitar ayuda', pathMatch: './pages/solicitud/*' },
        { title: 'Mis soportes', pathMatch: './pages/reporte-soporte' },
    ];

    hasSupportFeature: boolean = false;

    constructor(
        private sidebarService: NbSidebarService,
        private menuService: NbMenuService,
        private themeService: NbThemeService,
        private layoutService: LayoutService,
        private breakpointService: NbMediaBreakpointsService,
        private authService: AuthService,
        private router: Router,
        private store: Store<AppState>
    ) {}

    ngOnInit() {
        /*
    this.currentTheme = this.themeService.currentTheme;

    const { xl } = this.breakpointService.getBreakpointsMap();
    this.themeService
      .onMediaQueryChange()
      .pipe(
        map(([, currentBreakpoint]) => currentBreakpoint.width < xl),
        takeUntil(this.destroy$)
      )
      .subscribe(
        (isLessThanXl: boolean) => (this.userPictureOnly = isLessThanXl)
      );

    this.themeService
      .onThemeChange()
      .pipe(
        map(({ name }) => name),
        takeUntil(this.destroy$)
      )
      .subscribe((themeName) => (this.currentTheme = themeName));
      */
        this.store
            .pipe(select(notifications))
            .subscribe((notifications) => (this.notifications = notifications));
        const roles: [string] = JSON.parse(
            sessionStorage.getItem('user')
        ).roles.filter((r) => r.role != 'PROMOTOR');
        if (roles.length > 0) {
            this.hasSupportFeature = true;
        }
        this.menuService
            .onItemClick()
            .pipe(
                filter(({ tag }) => tag === 'support-menu'),
                map(({ item: { pathMatch } }) => pathMatch)
            )
            .subscribe((pathMatch) => this.router.navigate([pathMatch]));
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
        this.authService.logout().subscribe({
            error(e) {
                console.error('logout', e);
            },
            complete() {
                this.document.location.href =
                    'https://mail.google.com/mail/u/0/?logout&hl=en';
            },
        });
    }
}
