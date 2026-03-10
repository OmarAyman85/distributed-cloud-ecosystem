import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, throwError } from 'rxjs';

export interface User {
  id: number;
  email: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = '/api/auth';
  private tokenKey = 'savvy_token';
  private userKey = 'savvy_user';

  // Signals for reactive state
  currentUser = signal<User | null>(this.getUserFromStorage());
  isAuthenticated = signal<boolean>(!!this.getToken());

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    const payload = {
      appKey: 'SAVVY',
      identifier: credentials.email,
      password: credentials.password,
    };
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => this.handleAuthSuccess(response)),
      catchError((error) => {
        console.error('Login failed', error);
        return throwError(() => error);
      }),
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    const payload = {
      appKey: 'SAVVY',
      email: data.email,
      username: data.email, // using email as username
      password: data.password,
      firstName: data.firstName || 'User',
      lastName: data.lastName || 'Name',
      gender: 'PREFER_NOT_TO_SAY',
      mfaEnabled: false,
    };
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, payload)
      .pipe(
        tap((response) => this.handleAuthSuccess(response)),
        catchError((error) => {
          console.error('Registration failed', error);
          return throwError(() => error);
        }),
      );
  }

  loginWithToken(token: string): void {
    const user = this.decodeToken(token);
    this.handleAuthSuccess({ token, refreshToken: '', user });
  }

  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateProfile(updates: {
    firstName?: string;
    lastName?: string;
    email?: string;
  }): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/profile`, updates).pipe(
      tap((user) => {
        this.currentUser.set(user);
        localStorage.setItem(this.userKey, JSON.stringify(user));
      }),
    );
  }

  private decodeToken(token: string): User {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return {
        id: payload.id || 0,
        email: payload.sub || '',
        name:
          payload.name || payload.firstName
            ? payload.firstName + ' ' + payload.lastName
            : payload.sub || 'User',
      };
    } catch (e) {
      console.error('Failed to decode token', e);
      return { id: 0, email: '', name: 'User' };
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private handleAuthSuccess(response: any): void {
    const token = response.access_token || response.token;
    localStorage.setItem(this.tokenKey, token);

    // Decode user info from the JWT
    const user = this.decodeToken(token);
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUser.set(user);

    this.isAuthenticated.set(true);
    this.router.navigate(['/dashboard']);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }
}
