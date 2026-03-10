import { Component, inject, input, model } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { Widget } from '../../../models/dashboard';
import { DashboardService } from '../../../services/dashboard.service';

@Component({
  selector: 'app-widget-options',
  imports: [MatButtonModule, MatIcon, MatButtonToggleModule],
  templateUrl: './widget-options.component.html',
  styles: `
    :host {
      position: absolute;
      z-index: 2;
      background: var(--bg-surface, #ffffff);
      color: var(--text-main, #000000);
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
      color: var(--text-secondary, #554151);
      transition: background-color 0.3s;
    }

    .move-forward-button {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      right: 4px;
      color: var(--text-secondary, #554151);
      transition: background-color 0.3s;
    }

    .move-backward-button {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      left: 4px;
      color: var(--text-secondary, #554151);
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
  `,
})
export class WidgetOptionsComponent {
  data = input.required<Widget>();
  showOptions = model<boolean>(false);
  store = inject(DashboardService);
}
