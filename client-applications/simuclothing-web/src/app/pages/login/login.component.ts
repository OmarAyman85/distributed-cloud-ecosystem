import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div
      class="flex min-h-screen items-center justify-center bg-secondary px-4"
    >
      <div
        class="w-full max-w-md space-y-8 rounded-2xl border border-primary/30 bg-zinc-900 p-10 shadow-2xl shadow-primary/10"
      >
        <!-- Logo / Title -->
        <div class="text-center">
          <h1
            class="text-3xl font-extrabold text-primary tracking-widest uppercase"
          >
            SimuClothing
          </h1>
          <p class="mt-2 text-sm text-gray-400">Sign in to your account</p>
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-4">
          <!-- Username / Email -->
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1"
              >Username or Email</label
            >
            <input
              formControlName="username"
              type="text"
              class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
              placeholder="Enter username or email"
            />
          </div>

          <!-- Password -->
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1"
              >Password</label
            >
            <input
              formControlName="password"
              type="password"
              class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
              placeholder="Your password"
            />
          </div>

          <!-- Error Message -->
          @if (error()) {
            <div
              class="rounded-lg bg-red-900/30 border border-red-800 px-4 py-3 text-sm text-red-400"
            >
              {{ error() }}
            </div>
          }

          <!-- Submit Button -->
          <button
            type="submit"
            [disabled]="loginForm.invalid || isLoading()"
            class="w-full rounded-lg bg-primary px-4 py-3 text-sm font-bold text-black hover:bg-yellow-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed mt-2"
          >
            @if (isLoading()) {
              <span>Signing in...</span>
            } @else {
              <span>Sign In</span>
            }
          </button>
        </form>

        <p class="text-center text-sm text-gray-500">
          Don't have an account?
          <a
            routerLink="/register"
            class="font-semibold text-primary hover:text-yellow-400 transition-colors"
            >Create one</a
          >
        </p>
      </div>
    </div>
  `,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm = this.fb.group({
    username: ["", Validators.required],
    password: ["", Validators.required],
  });

  isLoading = signal(false);
  error = signal<string | null>(null);

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading.set(true);
      this.error.set(null);
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.router.navigate(["/"]);
        },
        error: () => {
          this.error.set("Invalid username or password");
          this.isLoading.set(false);
        },
      });
    }
  }
}
