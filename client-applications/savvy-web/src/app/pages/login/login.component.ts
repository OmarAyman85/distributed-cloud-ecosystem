import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
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
    MatCheckboxModule,
    MatSnackBarModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  hidePassword = true;
  isLoading = false;

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.authService.loginWithToken(token);
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.isLoading = false;
          this.snackBar.open('Login successful!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar'],
          });
          // Navigation handled by auth service
        },
        error: (error) => {
          this.isLoading = false;
          this.snackBar.open(
            'Login failed. Please check your credentials.',
            'Close',
            {
              duration: 5000,
              panelClass: ['error-snackbar'],
            },
          );
          console.error('Login error:', error);
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
