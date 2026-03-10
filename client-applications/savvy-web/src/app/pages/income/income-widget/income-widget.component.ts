import { Component, input, signal } from '@angular/core';
import { IncomeWidgetOptionsComponent } from '../income-widget-options/income-widget-options.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Widget } from '../../../models/dashboard';

@Component({
  selector: 'app-income-widget',
  imports: [MatButtonModule, MatIcon, IncomeWidgetOptionsComponent],
  standalone: true,
  templateUrl: './income-widget.component.html',
  styles: `
  :host {
    display: block;
    border-radius: 16px;
    color: #000000;
  }

  .container {
    position: relative;
    box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.4);
    height: 100%;
    width: 100%;
    padding: 32px;
    box-sizing: border-box;
    overflow: hidden;
    border: 1px solid #000000;
    border-radius: inherit;
  }

  .settings-button {
    position: absolute;
    top: 20px;
    right: 20px;
    color: rgb(85, 65, 81);
    transition: background-color 0.3s;
  }
  `,
  host: {
    '[style.grid-row]': '"span " + (data().rows ?? 1)',
    '[style.grid-column]': '"span " + (data().columns ?? 1)',
  },
})
export class IncomeWidgetComponent {
  data = input.required<Widget>();
  showOptions = signal(false);
}
