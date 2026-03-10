import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { IncomeService } from '../../../services/income.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-income-create',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  standalone: true,
  templateUrl: './income-create.component.html',
  providers: [IncomeService],
  styles: `
  .header {
    z-index: 2;
    background: white;
    color: black;
    position: absolute;
    top: 20%;
    left: 25%;
    box-sizing: border-box;
    box-shadow: 0 20px 20px 0 rgba(0, 0, 0, 0.4);
    padding: 16px;
    border-radius: 50px;
    border: 1px solid black;

    > p {
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 24px;
      font-weight: 600;
      color: #5b005b;
    }
  }

  .income-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-width: 800px;
    margin: auto;
  }

  .submit-button {
    background-color: #5b005b;
    color: white;
    cursor: pointer;
  }
  `,
})
export class IncomeCreateComponent {
  public incomeForm: FormGroup;
  private incomeService = inject(IncomeService);
  public router = inject(Router);

  constructor(private fb: FormBuilder) {
    this.incomeForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      category: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      date: ['', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.incomeForm.valid) {
      this.incomeService.createIncome(this.incomeForm.value).subscribe({
        next: (response) => {
          console.log('Income created:', response);
          this.incomeForm.reset();
          this.router.navigate(['/income']);
        },
        error: (error) => {
          console.error('Error creating income:', error);
        },
      });
    }
  }
}
