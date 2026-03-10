import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);
  user = this.authService.currentUser;

  isEditing = signal(false);
  editName = '';
  editEmail = '';
  isSaving = false;

  startEdit() {
    const u = this.user();
    this.editName = u?.name || '';
    this.editEmail = u?.email || '';
    this.isEditing.set(true);
  }

  cancelEdit() {
    this.isEditing.set(false);
  }

  saveProfile() {
    this.isSaving = true;
    const nameParts = this.editName.trim().split(' ');
    const firstName = nameParts[0];
    const lastName = nameParts.slice(1).join(' ') || firstName;

    this.authService
      .updateProfile({ firstName, lastName, email: this.editEmail })
      .subscribe({
        next: () => {
          this.isSaving = false;
          this.isEditing.set(false);
          this.snackBar.open('Profile updated!', 'Close', { duration: 3000 });
        },
        error: (err) => {
          this.isSaving = false;
          this.snackBar.open('Failed to update profile', 'Close', {
            duration: 3000,
          });
          console.error(err);
        },
      });
  }

  logout(): void {
    this.authService.logout();
  }
}
