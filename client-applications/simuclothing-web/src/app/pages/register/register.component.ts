import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-register",
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
          <p class="mt-2 text-sm text-gray-400">Create your account</p>
        </div>

        <form
          [formGroup]="registerForm"
          (ngSubmit)="onSubmit()"
          class="space-y-4"
        >
          <!-- Row: First + Last name -->
          <div class="flex gap-3">
            <div class="flex-1">
              <label class="block text-xs font-medium text-gray-400 mb-1"
                >First Name</label
              >
              <input
                formControlName="firstName"
                type="text"
                class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
                placeholder="First name"
              />
            </div>
            <div class="flex-1">
              <label class="block text-xs font-medium text-gray-400 mb-1"
                >Last Name</label
              >
              <input
                formControlName="lastName"
                type="text"
                class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
                placeholder="Last name"
              />
            </div>
          </div>

          <!-- Username -->
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1"
              >Username</label
            >
            <input
              formControlName="username"
              type="text"
              class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
              placeholder="Choose a username"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block text-xs font-medium text-gray-400 mb-1"
              >Email</label
            >
            <input
              formControlName="email"
              type="email"
              class="w-full rounded-lg bg-zinc-800 border border-zinc-700 px-3 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-sm"
              placeholder="your@email.com"
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
              placeholder="Min. 6 characters"
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
            [disabled]="registerForm.invalid || isLoading()"
            class="w-full rounded-lg bg-primary px-4 py-3 text-sm font-bold text-black hover:bg-yellow-400 transition-colors disabled:opacity-50 disabled:cursor-not-allowed mt-2"
          >
            @if (isLoading()) {
              <span>Creating account...</span>
            } @else {
              <span>Create Account</span>
            }
          </button>
        </form>

        <p class="text-center text-sm text-gray-500">
          Already have an account?
          <a
            routerLink="/login"
            class="font-semibold text-primary hover:text-yellow-400 transition-colors"
            >Sign in</a
          >
        </p>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm = this.fb.group({
    firstName: ["", Validators.required],
    lastName: ["", Validators.required],
    username: ["", Validators.required],
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(6)]],
    gender: ["PREFER_NOT_TO_SAY", Validators.required],
  });

  isLoading = signal(false);
  error = signal<string | null>(null);

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading.set(true);
      this.error.set(null);
      const payload = {
        ...this.registerForm.value,
        mfaEnabled: false,
      };
      this.authService.register(payload).subscribe({
        next: () => {
          this.router.navigate(["/"]);
        },
        error: (err) => {
          this.error.set(
            err.error?.message ||
              err.error ||
              "Registration failed. Please try again.",
          );
          this.isLoading.set(false);
        },
      });
    }
  }
}
