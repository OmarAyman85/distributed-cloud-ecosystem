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
  selector: 'app-add-investment-dialog',
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
  templateUrl: './add-investment-dialog.component.html',
})
export class AddInvestmentDialogComponent {
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<AddInvestmentDialogComponent>);
  data = inject(MAT_DIALOG_DATA, { optional: true });

  isEditing = !!this.data?.item;

  form: FormGroup = this.fb.group({
    title: [this.data?.item?.title || '', Validators.required],
    symbol: [this.data?.item?.symbol || '', Validators.required],
    amountInvested: [
      this.data?.item?.amountInvested || null,
      [Validators.required, Validators.min(0.01)],
    ],
    currentValue: [
      this.data?.item?.currentValue !== undefined
        ? this.data?.item?.currentValue
        : null,
      [Validators.required, Validators.min(0)],
    ],
    date: [
      this.data?.item?.date ? new Date(this.data?.item?.date) : new Date(),
      Validators.required,
    ],
    description: [this.data?.item?.description || ''],
  });

  onSubmit() {
    if (this.form.valid) {
      const rawValue = this.form.value;
      const d = new Date(rawValue.date);
      const formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

      const payload = {
        ...rawValue,
        date: formattedDate,
        amountInvested: Math.round(rawValue.amountInvested),
        currentValue: Math.round(rawValue.currentValue),
      };

      this.dialogRef.close(payload);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
