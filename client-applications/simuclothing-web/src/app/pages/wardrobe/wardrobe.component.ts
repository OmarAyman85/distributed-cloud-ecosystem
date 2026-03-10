import { Component, inject, signal, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FashionService, WardrobeItem } from "../../services/fashion.service";
import { NavbarComponent } from "../../components/navbar/navbar.component";

@Component({
  selector: "app-wardrobe",
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-secondary text-white">
      <app-navbar />

      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-10">
        <h1 class="text-3xl font-extrabold text-primary tracking-wide mb-2">
          My Wardrobe
        </h1>
        <p class="text-zinc-400 text-sm mb-8">
          Your owned items — clothes you've purchased.
        </p>

        <!-- Empty state -->
        @if (items().length === 0 && !isLoading()) {
          <div class="flex flex-col items-center justify-center py-24 gap-4">
            <svg
              class="w-16 h-16 text-zinc-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="1.5"
                d="M16.5 6v.75m0 3v.75m0 3V6.75A2.25 2.25 0 0014.25 4.5h-9A2.25 2.25 0 003 6.75v10.5A2.25 2.25 0 005.25 19.5h9a2.25 2.25 0 002.25-2.25V6.75m-9 0h9m-4.5-3v3"
              />
            </svg>
            <p class="text-zinc-400 text-lg">Your wardrobe is empty.</p>
            <a
              routerLink="/shop"
              class="mt-2 px-6 py-2 bg-primary text-black font-bold rounded-lg hover:bg-yellow-400 transition-colors"
              >Go Shopping</a
            >
          </div>
        }

        <!-- Loading -->
        @if (isLoading()) {
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
            @for (i of [1, 2, 3, 4]; track i) {
              <div class="rounded-xl bg-zinc-800 animate-pulse h-72"></div>
            }
          </div>
        }

        <!-- Grid -->
        @if (!isLoading() && items().length > 0) {
          <div class="grid grid-cols-2 gap-6 sm:grid-cols-3 lg:grid-cols-4">
            @for (item of items(); track item.id) {
              <div
                class="group relative rounded-xl border border-zinc-800 bg-zinc-900 overflow-hidden hover:border-primary/40 transition-colors"
              >
                <div class="aspect-[3/4] w-full overflow-hidden bg-zinc-800">
                  <img
                    [src]="getProductImage(item.product)"
                    [alt]="item.product.name"
                    class="h-full w-full object-cover object-center group-hover:scale-105 transition-transform duration-300"
                  />
                </div>
                <div class="p-3">
                  <h3 class="text-sm font-semibold text-white truncate">
                    {{ item.product.name }}
                  </h3>
                  <p class="text-xs text-zinc-400 mt-0.5">
                    {{ item.product.brand }}
                  </p>
                  <span
                    class="mt-2 inline-block text-xs font-bold text-green-400 bg-green-900/30 border border-green-800 px-2 py-0.5 rounded"
                  >
                    {{ item.status }}
                  </span>
                </div>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `,
})
export class WardrobeComponent implements OnInit {
  fashionService = inject(FashionService);
  items = signal<WardrobeItem[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    this.fashionService.getWardrobe().subscribe({
      next: (items) => {
        this.items.set(items);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  getProductImage(product: any): string {
    return product.imageUrls?.length > 0
      ? product.imageUrls[0]
      : "https://placehold.co/300x400?text=No+Image";
  }
}
