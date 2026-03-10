import { Component, inject } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-navbar",
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="bg-secondary border-b border-primary/20">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center gap-8">
            <!-- Brand -->
            <a
              routerLink="/"
              class="text-primary font-bold text-xl tracking-wider"
              >SimuClothing</a
            >

            <!-- Main nav (always visible) -->
            <div class="hidden md:flex items-baseline space-x-1">
              <a
                routerLink="/shop"
                routerLinkActive="bg-primary text-secondary"
                class="text-gray-300 hover:bg-primary/20 hover:text-primary rounded-md px-3 py-2 text-sm font-medium transition-colors"
                >Shop</a
              >

              @if (authService.currentUser()) {
                <a
                  routerLink="/wardrobe"
                  routerLinkActive="bg-primary text-secondary"
                  class="text-gray-300 hover:bg-primary/20 hover:text-primary rounded-md px-3 py-2 text-sm font-medium transition-colors"
                  >Wardrobe</a
                >
                <a
                  routerLink="/mimoji"
                  routerLinkActive="bg-primary text-secondary"
                  class="text-gray-300 hover:bg-primary/20 hover:text-primary rounded-md px-3 py-2 text-sm font-medium transition-colors"
                  >Mimoji</a
                >
              }
            </div>
          </div>

          <!-- Right side -->
          <div class="hidden md:flex items-center gap-3">
            @if (authService.currentUser()) {
              <!-- Logged in -->
              <span class="text-xs text-zinc-500">{{
                authService.currentUser()!.username
              }}</span>
              <a
                routerLink="/cart"
                class="text-gray-300 hover:text-primary transition-colors relative"
                title="Cart"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="w-6 h-6"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272M6 20.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm12.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z"
                  />
                </svg>
              </a>
              <button
                (click)="authService.logout()"
                class="text-sm font-medium text-gray-300 hover:text-primary transition-colors border border-zinc-700 hover:border-primary/40 rounded-md px-3 py-1.5"
              >
                Logout
              </button>
            } @else {
              <!-- Not logged in -->
              <a
                routerLink="/login"
                class="text-sm font-medium text-gray-300 hover:text-primary transition-colors px-3 py-1.5"
              >
                Sign In
              </a>
              <a
                routerLink="/register"
                class="text-sm font-bold bg-primary text-black hover:bg-yellow-400 transition-colors rounded-md px-4 py-1.5"
              >
                Register
              </a>
            }
          </div>
        </div>
      </div>
    </nav>
  `,
})
export class NavbarComponent {
  authService = inject(AuthService);
}
