import { Component, inject, input, model } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { Widget } from '../../../models/dashboard';
import { IncomeService } from '../../../services/income.service';

@Component({
  selector: 'app-income-widget-options',
  imports: [MatButtonModule, MatIcon, MatButtonToggleModule],
  standalone: true,
  templateUrl: './income-widget-options.component.html',
  styles: `
  :host {
    position: absolute;
    z-index: 2;
    background: whitesmoke;
    color: black;
    top: 0;
    left: 0;
    border-radius: inherit;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    --mat-standard-button-toggle-height: 36px;

    > div {
      display: flex;
      gap: 8px;
      align-items: center;
      margin-bottom: 8px;
    }
  }

  .close-button {
    position: absolute;
    top: 10px;
    right: 10px;
    color: rgb(85, 65, 81);
    transition: background-color 0.3s;
  }

  .move-forward-button {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    right: -5px;
    color: rgb(85, 65, 81);
    transition: background-color 0.3s;
  }

  .move-backward-button {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    left: -5px;
    color: rgb(85, 65, 81);
    transition: background-color 0.3s;
  }

  .remove-button {
    position: absolute;
    top: 25px;
    transform: translateY(-50%);
    left: 10px;
    color: #cc0000;
    transition: background-color 0.3s;
  }

  .edit-button {
    position: absolute;
    top: 25px;
    transform: translateY(-50%);
    left: 40px;
    color: #cc0000;
    transition: background-color 0.3s;
  }
  `,
})
export class IncomeWidgetOptionsComponent {
  data = input.required<Widget>();
  showOptions = model<boolean>(false);
  store = inject(IncomeService);

  deleteIncome(id: number): void {
    if (confirm('Are you sure you want to delete this income entry?')) {
      this.store.deleteIncome(id).subscribe({
        next: () => {
          this.store.removeWidget(id);
        },
        error: (err) => console.error('Error deleting income:', err),
      });
    }
  }
}
