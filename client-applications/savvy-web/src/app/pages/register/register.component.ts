import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  registerForm: FormGroup = this.fb.group(
    {
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: this.passwordMatchValidator },
  );

  hidePassword = true;
  hideConfirmPassword = true;
  isLoading = false;

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (
      password &&
      confirmPassword &&
      password.value !== confirmPassword.value
    ) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      return null;
    }
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      // We only send name, email, and password to the backend
      const { name, email, password } = this.registerForm.value;
      const nameParts = name.trim().split(' ');
      const firstName = nameParts[0];
      const lastName = nameParts.slice(1).join(' ') || nameParts[0]; // fallback to firstname if no lastname

      this.authService
        .register({ firstName, lastName, email, password })
        .subscribe({
          next: () => {
            this.isLoading = false;
            this.snackBar.open(
              'Registration successful! Welcome to Savvy.',
              'Close',
              {
                duration: 3000,
                panelClass: ['success-snackbar'],
              },
            );
            // navigation handled by handleAuthSuccess in authService
          },
          error: (error) => {
            this.isLoading = false;
            this.snackBar.open(
              'Registration failed. Please try again.',
              'Close',
              {
                duration: 5000,
                panelClass: ['error-snackbar'],
              },
            );
            console.error('Registration error:', error);
          },
        });
    }
  }

  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }

  loginWithApple() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/apple';
  }

  loginWithLinkedIn() {
    window.location.href =
      'http://localhost:8080/oauth2/authorization/linkedin';
  }
}
