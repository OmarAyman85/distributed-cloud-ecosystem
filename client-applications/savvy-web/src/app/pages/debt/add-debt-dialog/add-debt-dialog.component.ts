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
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-debt-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './add-debt-dialog.component.html',
})
export class AddDebtDialogComponent {
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<AddDebtDialogComponent>);
  data = inject(MAT_DIALOG_DATA, { optional: true });

  isEditing = !!this.data?.item;

  form: FormGroup = this.fb.group({
    title: [this.data?.item?.title || '', Validators.required],
    amount: [
      this.data?.item?.amount || null,
      [Validators.required, Validators.min(0.01)],
    ],
    dueDate: [
      this.data?.item?.dueDate
        ? new Date(this.data?.item?.dueDate)
        : new Date(),
      Validators.required,
    ],
    description: [this.data?.item?.description || ''],
  });

  onSubmit() {
    if (this.form.valid) {
      const rawValue = this.form.value;
      const d = new Date(rawValue.dueDate);
      const formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

      const payload = {
        ...rawValue,
        dueDate: formattedDate,
        amount: Math.round(rawValue.amount),
      };

      this.dialogRef.close(payload);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
