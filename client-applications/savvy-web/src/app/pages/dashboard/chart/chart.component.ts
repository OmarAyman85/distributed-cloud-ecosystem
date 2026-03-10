import {
  Component,
  inject,
  OnInit,
  ViewChildren,
  QueryList,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { DashboardService } from '../../../services/dashboard.service';
import { GraphDTO } from '../../../models/dashboard';

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
})
export class ChartComponent implements OnInit {
  private store = inject(DashboardService);

  @ViewChildren(BaseChartDirective) charts!: QueryList<BaseChartDirective>;

  // --- Bar Chart: Monthly Income vs Expense ---
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: '#718096' },
      },
      y: {
        min: 0,
        grid: { color: 'rgba(113, 128, 150, 0.1)' },
        ticks: { color: '#718096' },
      },
    },
    plugins: {
      legend: {
        display: true,
        position: 'top',
        labels: { color: '#718096', usePointStyle: true, padding: 16 },
      },
    },
  };
  public barChartType: ChartType = 'bar';

  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Income',
        backgroundColor: 'rgba(106, 170, 142, 0.7)',
        hoverBackgroundColor: 'rgba(106, 170, 142, 1)',
        borderRadius: 6,
      },
      {
        data: [],
        label: 'Expense',
        backgroundColor: 'rgba(201, 107, 107, 0.7)',
        hoverBackgroundColor: 'rgba(201, 107, 107, 1)',
        borderRadius: 6,
      },
    ],
  };

  // --- Doughnut Chart: Expense by Category ---
  public doughnutChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right',
        labels: {
          color: '#718096',
          usePointStyle: true,
          padding: 12,
          font: { size: 12 },
        },
      },
    },
  };
  public doughnutChartType: ChartType = 'doughnut';

  private categoryColors = [
    'rgba(91, 122, 157, 0.75)', // Steel Blue
    'rgba(201, 107, 107, 0.75)', // Rose
    'rgba(106, 170, 142, 0.75)', // Sage
    'rgba(212, 165, 90, 0.75)', // Gold
    'rgba(139, 142, 194, 0.75)', // Lavender
    'rgba(157, 122, 91, 0.75)', // Warm Brown
    'rgba(122, 157, 144, 0.75)', // Teal Green
    'rgba(170, 106, 142, 0.75)', // Mauve
  ];

  public doughnutChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: this.categoryColors,
        hoverBackgroundColor: this.categoryColors.map((c) =>
          c.replace('0.75', '1'),
        ),
        borderWidth: 2,
        borderColor: 'rgba(255,255,255,0.5)',
      },
    ],
  };

  // --- Line Chart: Net Savings Trend ---
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: '#718096' },
      },
      y: {
        grid: { color: 'rgba(113, 128, 150, 0.1)' },
        ticks: { color: '#718096' },
      },
    },
    plugins: {
      legend: {
        display: true,
        position: 'top',
        labels: { color: '#718096', usePointStyle: true, padding: 16 },
      },
    },
  };
  public lineChartType: ChartType = 'line';

  public lineChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Net Savings',
        borderColor: 'rgba(91, 122, 157, 1)',
        backgroundColor: 'rgba(91, 122, 157, 0.1)',
        fill: true,
        tension: 0.4,
        pointBackgroundColor: 'rgba(91, 122, 157, 1)',
        pointBorderColor: '#fff',
        pointRadius: 4,
      },
    ],
  };

  ngOnInit(): void {
    this.store.getChartData().subscribe((data) => {
      this.processData(data);
    });
  }

  private processData(data: GraphDTO) {
    const monthNames = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ];

    // --- Bar Chart: Monthly aggregation ---
    const incomeByMonth = new Array(12).fill(0);
    const expenseByMonth = new Array(12).fill(0);

    data.incomeList.forEach((inc) => {
      const d = new Date(inc.date);
      incomeByMonth[d.getMonth()] += inc.amount;
    });

    data.expenseList.forEach((exp) => {
      const d = new Date(exp.date);
      expenseByMonth[d.getMonth()] += exp.amount;
    });

    this.barChartData.labels = monthNames;
    this.barChartData.datasets[0].data = incomeByMonth;
    this.barChartData.datasets[1].data = expenseByMonth;

    // --- Doughnut Chart: Expense by category ---
    const categoryMap = new Map<string, number>();
    data.expenseList.forEach((exp) => {
      const cat = exp.category || 'Other';
      categoryMap.set(cat, (categoryMap.get(cat) || 0) + exp.amount);
    });

    this.doughnutChartData.labels = Array.from(categoryMap.keys());
    this.doughnutChartData.datasets[0].data = Array.from(categoryMap.values());

    // --- Line Chart: Net savings per month ---
    const netSavings = incomeByMonth.map((inc, i) => inc - expenseByMonth[i]);
    // Cumulative savings
    const cumulative: number[] = [];
    netSavings.reduce((acc, val, i) => {
      cumulative[i] = acc + val;
      return cumulative[i];
    }, 0);

    this.lineChartData.labels = monthNames;
    this.lineChartData.datasets[0].data = cumulative;

    // Update all charts
    this.charts?.forEach((c) => c.update());
  }
}
