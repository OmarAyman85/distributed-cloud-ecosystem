import { Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { tap } from "rxjs/operators";
import { AuthResponse, User } from "../models/auth.model";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = "http://localhost:8080/api/auth"; // Gateway URL

  currentUser = signal<User | null>(this.getUserFromStorage());

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: any) {
    const payload = {
      ...credentials,
      identifier: credentials.username || credentials.identifier,
      appKey: "SIMUCLOTHING",
    };
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, payload)
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  register(data: any) {
    const payload = { ...data, appKey: "SIMUCLOTHING" };
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, payload)
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    this.currentUser.set(null);
    this.router.navigate(["/login"]);
  }

  private handleAuthResponse(response: AuthResponse) {
    const token = (response as any).access_token || response.accessToken;
    const username = (response as any).user_name || response.username;
    localStorage.setItem("token", token);
    const user: User = {
      username: username,
      email: response.email,
      roles: response.roles,
    };
    localStorage.setItem("user", JSON.stringify(user));
    this.currentUser.set(user);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem("user");
    return userStr ? JSON.parse(userStr) : null;
  }

  getToken(): string | null {
    return localStorage.getItem("token");
  }
}
