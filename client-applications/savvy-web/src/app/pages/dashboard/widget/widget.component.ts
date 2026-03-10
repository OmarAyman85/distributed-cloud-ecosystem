import { Component, input, signal } from '@angular/core';
import { Widget } from '../../../models/dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { WidgetOptionsComponent } from '../widget-options/widget-options.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-widget',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    WidgetOptionsComponent,
  ],
  templateUrl: './widget.component.html',
  styleUrl: './widget.component.scss',
  host: {
    '[style.grid-row]': '"span " + (data().rows ?? 1)',
    '[style.grid-column]': '"span " + (data().columns ?? 1)',
  },
})
export class WidgetComponent {
  data = input.required<Widget>();
  showOptions = signal(false);

  getIcon(category: string): string {
    const map: Record<string, string> = {
      Income: 'arrow_upward',
      Expense: 'arrow_downward',
      Dashboard: 'dashboard',
      Savings: 'savings',
    };
    return map[category] || 'pie_chart';
  }
}
