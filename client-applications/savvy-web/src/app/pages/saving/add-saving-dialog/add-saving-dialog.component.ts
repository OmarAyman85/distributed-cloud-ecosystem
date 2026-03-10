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
  selector: 'app-add-saving-dialog',
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
  templateUrl: './add-saving-dialog.component.html',
})
export class AddSavingDialogComponent {
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<AddSavingDialogComponent>);
  data = inject(MAT_DIALOG_DATA, { optional: true });

  isEditing = !!this.data?.item;

  form: FormGroup = this.fb.group(
    {
      title: [this.data?.item?.title || '', Validators.required],
      targetAmount: [
        this.data?.item?.targetAmount || null,
        [Validators.required, Validators.min(0.01)],
      ],
      currentAmount: [
        this.data?.item?.currentAmount || 0,
        [Validators.required, Validators.min(0)],
      ],
      targetDate: [
        this.data?.item?.targetDate
          ? new Date(this.data?.item?.targetDate)
          : new Date(),
        Validators.required,
      ],
      description: [this.data?.item?.description || ''],
    },
    { validators: this.amountValidator },
  );

  amountValidator(g: FormGroup) {
    const target = g.get('targetAmount')?.value;
    const current = g.get('currentAmount')?.value;
    return target !== null && current !== null && current > target
      ? { currentExceedsTarget: true }
      : null;
  }

  onSubmit() {
    if (this.form.valid) {
      const rawValue = this.form.value;
      const d = new Date(rawValue.targetDate);
      const formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

      const payload = {
        ...rawValue,
        targetDate: formattedDate,
        targetAmount: Math.round(rawValue.targetAmount),
        currentAmount: Math.round(rawValue.currentAmount),
      };

      this.dialogRef.close(payload);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
