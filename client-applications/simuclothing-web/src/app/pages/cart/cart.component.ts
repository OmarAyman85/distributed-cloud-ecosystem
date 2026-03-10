import { Component, inject, signal, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FashionService, Cart } from "../../services/fashion.service";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { Router } from "@angular/router";

@Component({
  selector: "app-cart",
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-secondary text-white">
      <app-navbar />

      <div class="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-10">
        <h1 class="text-3xl font-extrabold text-primary tracking-wide mb-8">
          Shopping Cart
        </h1>

        <!-- Empty state -->
        @if (!cart() || cart()!.items.length === 0) {
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
                d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272M6 20.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm12.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z"
              />
            </svg>
            <p class="text-zinc-400 text-lg">Your cart is empty.</p>
            <a
              routerLink="/shop"
              class="mt-2 px-6 py-2 bg-primary text-black font-bold rounded-lg hover:bg-yellow-400 transition-colors"
              >Continue Shopping</a
            >
          </div>
        }

        <!-- Cart items -->
        @if (cart() && cart()!.items.length > 0) {
          <div class="flex flex-col gap-4">
            @for (item of cart()!.items; track item.id) {
              <div
                class="flex items-center gap-5 rounded-xl border border-zinc-800 bg-zinc-900 p-4"
              >
                <!-- Product image -->
                <div
                  class="h-24 w-24 flex-shrink-0 overflow-hidden rounded-lg border border-zinc-700"
                >
                  <img
                    [src]="getProductImage(item.product)"
                    [alt]="item.product.name"
                    class="h-full w-full object-cover object-center"
                  />
                </div>

                <!-- Info -->
                <div class="flex flex-1 flex-col gap-1">
                  <h3 class="text-base font-semibold text-white">
                    {{ item.product.name }}
                  </h3>
                  <p class="text-sm text-zinc-400">{{ item.product.brand }}</p>
                  <p class="text-sm text-zinc-500">Qty: {{ item.quantity }}</p>
                </div>

                <!-- Price + Remove -->
                <div class="flex flex-col items-end gap-2">
                  <p class="text-lg font-bold text-primary">
                    \${{ item.product.price * item.quantity }}
                  </p>
                  <button
                    (click)="removeItem(item.product.id)"
                    class="text-xs text-red-400 hover:text-red-300 transition-colors font-medium"
                  >
                    Remove
                  </button>
                </div>
              </div>
            }

            <!-- Summary -->
            <div
              class="mt-4 rounded-xl border border-primary/30 bg-zinc-900 p-6"
            >
              <div class="flex justify-between text-base font-medium mb-1">
                <span class="text-zinc-400">Subtotal</span>
                <span class="text-white font-bold"
                  >\${{ cart()!.totalAmount }}</span
                >
              </div>
              <p class="text-xs text-zinc-500 mb-6">
                Shipping and taxes calculated at checkout.
              </p>

              @if (checkoutSuccess()) {
                <div
                  class="rounded-lg bg-green-900/30 border border-green-700 px-4 py-3 text-sm text-green-400 mb-4"
                >
                  ✅ Order placed! Items moved to your Wardrobe.
                </div>
              }

              <button
                (click)="checkout()"
                [disabled]="isCheckingOut()"
                class="w-full rounded-lg bg-primary px-6 py-3 text-base font-bold text-black hover:bg-yellow-400 transition-colors disabled:opacity-50"
              >
                {{ isCheckingOut() ? "Processing..." : "Checkout" }}
              </button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
})
export class CartComponent implements OnInit {
  fashionService = inject(FashionService);
  router = inject(Router);
  cart = signal<Cart | null>(null);
  isCheckingOut = signal(false);
  checkoutSuccess = signal(false);

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.fashionService.getCart().subscribe({
      next: (cart) => this.cart.set(cart),
      error: () => this.cart.set({ items: [], totalAmount: 0 }),
    });
  }

  removeItem(productId: number) {
    this.fashionService.removeFromCart(productId).subscribe((cart) => {
      this.cart.set(cart);
    });
  }

  getProductImage(product: any): string {
    return product.imageUrls?.length > 0
      ? product.imageUrls[0]
      : "https://placehold.co/100x100?text=No+Image";
  }

  checkout() {
    this.isCheckingOut.set(true);
    this.fashionService.checkout().subscribe({
      next: () => {
        this.checkoutSuccess.set(true);
        this.loadCart();
        this.isCheckingOut.set(false);
        setTimeout(() => this.router.navigate(["/wardrobe"]), 1500);
      },
      error: () => {
        this.isCheckingOut.set(false);
      },
    });
  }
}
