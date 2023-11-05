import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(this.getUserFromLocalStorage());
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string) {
    return this.http.post<any>(`http://localhost:8080/authenticate`, { username, password })
      .pipe(map(response => {
        const token = response.jwt;
        if (token) {
          this.setUserInLocalStorage({ username, token });
          this.currentUserSubject.next({ username, token });
        }
        return response;
      }));
  }

  logout() {
    this.removeUserFromLocalStorage();
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    const currentUser = this.getUserFromLocalStorage();
    const token = currentUser ? currentUser.token : null;
    return !!token;
  }

  private getUserFromLocalStorage() {
    try {
      const currentUser = localStorage.getItem('currentUser');
      return currentUser ? JSON.parse(currentUser) : null;
    } catch (e) {
      console.error('Error reading from localStorage', e);
      return null;
    }
  }

  private setUserInLocalStorage(user: { username: string; token: string }) {
    try {
      localStorage.setItem('currentUser', JSON.stringify(user));
    } catch (e) {
      console.error('Error saving to localStorage', e);
    }
  }

  private removeUserFromLocalStorage() {
    try {
      localStorage.removeItem('currentUser');
    } catch (e) {
      console.error('Error removing from localStorage', e);
    }
  }

  // TODO: Implement a method to check if the token is expired
  private isTokenExpired(token: string): boolean {
    return false;
  }
}
