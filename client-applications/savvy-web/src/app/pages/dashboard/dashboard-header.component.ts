import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatMenuModule],
  template: `
    <div
      class="glass-card flex justify-between items-end mb-8 p-6 hover-lift max-sm:flex-col max-sm:items-start max-sm:gap-4"
    >
      <div>
        <h1 class="text-gradient mb-2 leading-tight max-sm:text-3xl">
          Good Morning, {{ userName }}!
        </h1>
        <p
          class="inline-flex items-center bg-primary/10 px-4 py-1.5 rounded-full text-primary-dark font-semibold text-sm m-0"
        >
          <mat-icon class="!text-base !w-4 !h-4 mr-2">calendar_today</mat-icon>
          {{ currentDate | date: 'fullDate' }}
        </p>
      </div>

      <div class="max-sm:w-full">
        <button
          mat-raised-button
          color="primary"
          [mat-menu-trigger-for]="widgetMenu"
          class="!h-12 !px-6 !rounded-[14px] !text-base max-sm:!w-full"
        >
          <mat-icon>add</mat-icon> Add Widget
        </button>

        <mat-menu #widgetMenu="matMenu" xPosition="before">
          <button
            mat-menu-item
            *ngFor="let wi of store.widgetsToAdd(); trackBy: trackById"
            (click)="store.addWidget(wi)"
          >
            <mat-icon>widgets</mat-icon> {{ wi.title }}
          </button>
          <button
            mat-menu-item
            disabled
            *ngIf="store.widgetsToAdd().length === 0"
          >
            No more widgets
          </button>
        </mat-menu>
      </div>
    </div>
  `,
  styles: [],
})
export class DashboardHeaderComponent {
  auth = inject(AuthService);
  store = inject(DashboardService);

  currentDate = new Date();

  get userName() {
    return this.auth.currentUser()?.name?.split(' ')[0] || 'User';
  }

  trackById(index: number, item: any) {
    return item.id;
  }
}
