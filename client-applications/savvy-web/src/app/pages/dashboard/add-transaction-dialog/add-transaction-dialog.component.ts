import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MatDialogRef,
  MatDialogModule,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonModule } from '@angular/common'; // Import CommonModule

@Component({
  selector: 'app-add-transaction-dialog',
  standalone: true,
  imports: [
    CommonModule, // Add CommonModule here
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './add-transaction-dialog.component.html',
  styleUrl: './add-transaction-dialog.component.scss',
})
export class AddTransactionDialogComponent {
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<AddTransactionDialogComponent>);
  data = inject(MAT_DIALOG_DATA, { optional: true });

  form: FormGroup = this.fb.group({
    title: ['', Validators.required],
    amount: [null, [Validators.required, Validators.min(0.01)]],
    category: ['', Validators.required],
    date: [new Date(), Validators.required],
    type: [this.data?.type || 'expense', Validators.required],
    description: [''],
  });

  categories = [
    'Food',
    'Transport',
    'Utilities',
    'Entertainment',
    'Health',
    'Salary',
    'Investment',
    'Freelance',
    'Other',
  ];

  onSubmit() {
    if (this.form.valid) {
      const rawValue = this.form.value;
      const d = new Date(rawValue.date);
      const formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

      const payload = {
        ...rawValue,
        date: formattedDate,
        amount: Math.round(rawValue.amount), // Ensure Integer
      };

      this.dialogRef.close(payload);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
