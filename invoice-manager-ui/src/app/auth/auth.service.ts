import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { User } from '../@core/models/user';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    constructor(private httpClient: HttpClient) {}

    public async getUserInfo(): Promise<User> {
        return new Promise((resolve) => {
            if (sessionStorage.getItem('user') !== undefined) {
                const user: User = JSON.parse(sessionStorage.getItem('user'));
                resolve(user);
            } else {
                this.httpClient
                    .get<User>('../api/users/myInfo')
                    .subscribe((user) => {
                        sessionStorage.setItem('email', user.email);
                        sessionStorage.setItem('user', JSON.stringify(user));
                        resolve(user);
                    });
            }
        });
    }

    public logout(): Observable<any> {
        return this.httpClient.post('../api/logout', {});
    }

    isUserLoggedIn(): Observable<boolean> {
        return this.httpClient.get<User>('../api/users/myInfo').pipe(
            tap((user: User) => {
                sessionStorage.setItem('email', user.email);
                sessionStorage.setItem('user', JSON.stringify(user));
            }),
            map((user: User) => user.activo)
        );
    }
}
