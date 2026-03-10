import { Component, computed, signal, inject } from '@angular/core';
import {
  Router,
  RouterOutlet,
  RouterModule,
  NavigationEnd,
} from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CustomSidenavComponent } from './components/custom-sidenav/custom-sidenav.component';
import { AuthService } from './services/auth.service';
import { ThemeService } from './services/theme.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatTooltipModule,
    CustomSidenavComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  themeService = inject(ThemeService);

  isAuthenticated = this.authService.isAuthenticated;

  // Track current route to hide sidebar on auth pages
  private currentUrl = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map((event) => (event as NavigationEnd).urlAfterRedirects),
      startWith(this.router.url),
    ),
    { initialValue: this.router.url },
  );

  // Returns true if we're on /login or /register
  isAuthRoute = computed(() => {
    const url = this.currentUrl();
    return url.startsWith('/login') || url.startsWith('/register');
  });

  // Show shell only when authenticated AND not on auth routes AND not on home route
  showAppShell = computed(() => {
    const url = this.currentUrl();
    const isHome = url === '/' || url === '';
    return this.isAuthenticated() && !this.isAuthRoute() && !isHome;
  });

  collapsed = signal<boolean>(false);
  sideNavWidth = computed(() => (this.collapsed() ? '65px' : '250px'));

  toggleTheme(): void {
    this.themeService.toggle();
  }
}
