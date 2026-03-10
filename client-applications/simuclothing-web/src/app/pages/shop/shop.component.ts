import { Component, inject, signal, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FashionService, Product } from "../../services/fashion.service";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { FormsModule } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";

@Component({
  selector: "app-shop",
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, RouterLink],
  styles: [
    `
      .product-card {
        transition:
          transform 0.3s ease,
          box-shadow 0.3s ease;
      }
      .product-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 20px 40px rgba(212, 175, 55, 0.15);
      }
      .product-img {
        transition: transform 0.5s ease;
      }
      .product-card:hover .product-img {
        transform: scale(1.06);
      }
      .quick-add {
        opacity: 0;
        transform: translateY(8px);
        transition:
          opacity 0.25s ease,
          transform 0.25s ease;
      }
      .product-card:hover .quick-add {
        opacity: 1;
        transform: translateY(0);
      }
      .hero-text {
        animation: fadeUp 0.9s ease both;
      }
      @keyframes fadeUp {
        from {
          opacity: 0;
          transform: translateY(24px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }
      .filter-chip {
        transition: all 0.2s ease;
        cursor: pointer;
      }
      .filter-chip.active {
        background: #d4af37;
        color: #0a0a0a;
        border-color: #d4af37;
      }
      .hero-gradient {
        background: linear-gradient(
          135deg,
          #0a0a0a 0%,
          #1a1100 50%,
          #0a0a0a 100%
        );
      }
    `,
  ],
  template: `
    <div class="min-h-screen bg-zinc-950 text-white">
      <app-navbar />

      <!-- ═══════════════════ HERO ═══════════════════ -->
      <section class="relative hero-gradient overflow-hidden">
        <div
          class="absolute inset-0 opacity-10"
          style="background-image: repeating-linear-gradient(45deg, #D4AF37 0, #D4AF37 1px, transparent 0, transparent 50%); background-size: 20px 20px;"
        ></div>
        <div
          class="relative mx-auto max-w-7xl px-6 py-20 lg:py-28 flex items-center justify-between gap-12"
        >
          <div class="max-w-xl hero-text">
            <span
              class="text-xs font-bold tracking-[0.3em] text-primary/70 uppercase mb-4 block"
              >New Collection · 2026</span
            >
            <h1 class="text-5xl lg:text-7xl font-black leading-none mb-6">
              Dress<br />
              <span class="text-primary">Beyond</span><br />
              Limits.
            </h1>
            <p class="text-zinc-400 text-lg mb-8 leading-relaxed">
              Premium fashion curated for the bold. Every piece tells a story —
              make yours unforgettable.
            </p>
            <div class="flex gap-4">
              <button
                (click)="scrollToGrid()"
                class="bg-primary text-black px-8 py-3.5 font-bold text-sm tracking-wider uppercase rounded hover:bg-yellow-400 transition-colors"
              >
                Shop Now
              </button>
              <a
                routerLink="/wardrobe"
                class="border border-zinc-700 px-8 py-3.5 text-sm tracking-wider uppercase rounded hover:border-primary hover:text-primary transition-colors"
              >
                My Wardrobe
              </a>
            </div>
          </div>
          <!-- Stats -->
          <div class="hidden lg:flex flex-col gap-8 text-right">
            @for (stat of stats; track stat.label) {
              <div>
                <div class="text-4xl font-black text-primary">
                  {{ stat.value }}
                </div>
                <div
                  class="text-xs text-zinc-500 uppercase tracking-widest mt-1"
                >
                  {{ stat.label }}
                </div>
              </div>
            }
          </div>
        </div>
        <!-- Gold bottom line -->
        <div
          class="absolute bottom-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-primary/50 to-transparent"
        ></div>
      </section>

      <!-- ═══════════════════ CATEGORY PILLS ═══════════════════ -->
      <section
        class="border-b border-zinc-800 sticky top-16 z-30 bg-zinc-950/95 backdrop-blur-md"
      >
        <div class="mx-auto max-w-7xl px-6">
          <div
            class="flex items-center gap-2 py-3 overflow-x-auto scrollbar-hidden"
          >
            @for (cat of categories; track cat.value) {
              <button
                (click)="setCategory(cat.value)"
                [class.active]="filters.category === cat.value"
                class="filter-chip flex-shrink-0 border border-zinc-700 text-zinc-400 px-4 py-1.5 rounded-full text-xs font-semibold uppercase tracking-wider hover:border-primary hover:text-primary"
              >
                {{ cat.label }}
              </button>
            }

            <!-- Size quick filters -->
            <div
              class="ml-auto flex-shrink-0 flex items-center gap-2 border-l border-zinc-800 pl-4"
            >
              <span class="text-xs text-zinc-600 uppercase tracking-wider"
                >Size:</span
              >
              @for (sz of ["S", "M", "L", "XL"]; track sz) {
                <button
                  (click)="setSize(sz)"
                  [class.active]="filters.size === sz"
                  class="filter-chip w-9 h-9 border border-zinc-700 rounded text-xs font-bold text-zinc-400 hover:border-primary hover:text-primary"
                >
                  {{ sz }}
                </button>
              }
              @if (filters.size) {
                <button
                  (click)="setSize('')"
                  class="text-xs text-zinc-600 hover:text-red-400 transition-colors ml-1"
                >
                  ✕
                </button>
              }
            </div>
          </div>
        </div>
      </section>

      <!-- ═══════════════════ PRODUCT GRID ═══════════════════ -->
      <section id="product-grid" class="mx-auto max-w-7xl px-6 py-12">
        <!-- Results header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h2 class="text-2xl font-bold">
              {{
                filters.category
                  ? getCategoryLabel(filters.category)
                  : "All Products"
              }}
            </h2>
            <p class="text-zinc-500 text-sm mt-1">
              {{ products().length }} items
            </p>
          </div>
          <div class="text-xs text-zinc-600 uppercase tracking-wider">
            {{ filters.size ? "Size " + filters.size : "All Sizes" }}
          </div>
        </div>

        <!-- Loading skeletons -->
        @if (isLoading()) {
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
            @for (i of [1, 2, 3, 4, 5, 6, 7, 8]; track i) {
              <div class="rounded-xl bg-zinc-900 animate-pulse">
                <div class="aspect-[3/4] rounded-xl bg-zinc-800"></div>
                <div class="p-4 space-y-2">
                  <div class="h-4 bg-zinc-800 rounded w-3/4"></div>
                  <div class="h-3 bg-zinc-800 rounded w-1/2"></div>
                </div>
              </div>
            }
          </div>
        }

        <!-- Empty state -->
        @if (!isLoading() && products().length === 0) {
          <div class="flex flex-col items-center justify-center py-32 gap-4">
            <div class="text-6xl">🔍</div>
            <p class="text-zinc-400 text-lg">No products found.</p>
            <button
              (click)="clearFilters()"
              class="text-primary hover:underline text-sm"
            >
              Clear filters
            </button>
          </div>
        }

        <!-- Products -->
        @if (!isLoading() && products().length > 0) {
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-5">
            @for (product of products(); track product.id) {
              <div
                class="product-card group relative rounded-xl overflow-hidden bg-zinc-900 border border-zinc-800 cursor-pointer"
                (click)="openDetail(product)"
              >
                <!-- Image -->
                <div class="relative aspect-[3/4] overflow-hidden bg-zinc-800">
                  <img
                    [src]="getProductImage(product)"
                    [alt]="product.name"
                    class="product-img h-full w-full object-cover object-center"
                  />

                  <!-- Overlay gradient -->
                  <div
                    class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                  ></div>

                  <!-- Category badge -->
                  <div class="absolute top-3 left-3">
                    <span
                      class="bg-black/70 backdrop-blur text-primary text-[10px] font-bold uppercase tracking-wider px-2 py-1 rounded"
                    >
                      {{ product.category }}
                    </span>
                  </div>

                  <!-- Quick add (appears on hover) -->
                  <div class="quick-add absolute bottom-3 left-3 right-3">
                    <button
                      (click)="addToCart(product, $event)"
                      class="w-full bg-primary text-black font-bold py-2.5 rounded text-xs uppercase tracking-widest hover:bg-yellow-400 transition-colors"
                    >
                      + Add to Cart
                    </button>
                  </div>
                </div>

                <!-- Info -->
                <div class="p-4">
                  <h3
                    class="text-sm font-semibold text-white truncate group-hover:text-primary transition-colors"
                  >
                    {{ product.name }}
                  </h3>
                  <p class="text-xs text-zinc-500 mt-0.5">
                    {{ product.brand }}
                  </p>
                  <div class="flex items-center justify-between mt-3">
                    <span class="text-base font-bold text-primary"
                      >\${{ product.price }}</span
                    >
                    <!-- Size chips -->
                    <div class="flex gap-1">
                      @for (s of getDisplaySizes(product); track s) {
                        <span
                          class="text-[10px] border border-zinc-700 text-zinc-500 px-1.5 py-0.5 rounded"
                          >{{ s }}</span
                        >
                      }
                    </div>
                  </div>
                </div>
              </div>
            }
          </div>
        }
      </section>

      <!-- ═══════════════════ PRODUCT DETAIL MODAL ═══════════════════ -->
      @if (selectedProduct()) {
        <div
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
          (click)="closeDetail()"
        >
          <div class="absolute inset-0 bg-black/80 backdrop-blur-sm"></div>
          <div
            class="relative bg-zinc-900 rounded-2xl border border-zinc-700 max-w-3xl w-full max-h-[90vh] overflow-y-auto shadow-2xl shadow-primary/10"
            (click)="$event.stopPropagation()"
          >
            <button
              (click)="closeDetail()"
              class="absolute top-4 right-4 z-10 w-8 h-8 rounded-full bg-zinc-800 hover:bg-zinc-700 flex items-center justify-center text-zinc-400 hover:text-white transition-colors"
            >
              ✕
            </button>

            <div class="grid grid-cols-1 sm:grid-cols-2">
              <!-- Image -->
              <div class="aspect-[4/5] sm:aspect-auto sm:h-full">
                <img
                  [src]="getProductImage(selectedProduct()!)"
                  [alt]="selectedProduct()!.name"
                  class="w-full h-full object-cover object-center rounded-l-2xl"
                />
              </div>

              <!-- Info -->
              <div class="p-8 flex flex-col gap-5">
                <div>
                  <span
                    class="text-xs font-bold text-primary/70 uppercase tracking-widest"
                    >{{ selectedProduct()!.category }}</span
                  >
                  <h2 class="text-2xl font-black mt-1">
                    {{ selectedProduct()!.name }}
                  </h2>
                  <p class="text-zinc-500 text-sm mt-1">
                    by {{ selectedProduct()!.brand }}
                  </p>
                </div>

                <div class="text-3xl font-black text-primary">
                  \${{ selectedProduct()!.price }}
                </div>

                <p class="text-zinc-400 text-sm leading-relaxed">
                  {{
                    selectedProduct()!.description ||
                      "Premium quality piece, crafted with attention to detail."
                  }}
                </p>

                <!-- Available sizes -->
                @if (selectedProduct()!.availableSizes?.length) {
                  <div>
                    <p
                      class="text-xs font-bold text-zinc-500 uppercase tracking-wider mb-2"
                    >
                      Select Size
                    </p>
                    <div class="flex gap-2 flex-wrap">
                      @for (s of selectedProduct()!.availableSizes; track s) {
                        <button
                          (click)="selectedSize.set(s)"
                          [class.border-primary]="selectedSize() === s"
                          [class.text-primary]="selectedSize() === s"
                          class="border border-zinc-700 text-zinc-400 px-4 py-2 rounded text-sm font-bold hover:border-primary hover:text-primary transition-colors"
                        >
                          {{ s }}
                        </button>
                      }
                    </div>
                  </div>
                }

                <!-- Colors -->
                @if (selectedProduct()!.colors?.length) {
                  <div>
                    <p
                      class="text-xs font-bold text-zinc-500 uppercase tracking-wider mb-2"
                    >
                      Colors
                    </p>
                    <div class="flex gap-2">
                      @for (c of selectedProduct()!.colors; track c) {
                        <span
                          class="text-xs border border-zinc-700 text-zinc-400 px-3 py-1 rounded-full"
                          >{{ c }}</span
                        >
                      }
                    </div>
                  </div>
                }

                <div class="mt-auto pt-4 flex flex-col gap-3">
                  <button
                    (click)="addToCart(selectedProduct()!, $event)"
                    class="w-full bg-primary text-black font-black py-4 rounded-lg text-sm uppercase tracking-widest hover:bg-yellow-400 active:scale-[0.98] transition-all"
                  >
                    Add to Cart
                  </button>
                  <button
                    (click)="closeDetail()"
                    class="w-full border border-zinc-700 text-zinc-400 py-3 rounded-lg text-sm hover:border-zinc-500 transition-colors"
                  >
                    Continue Shopping
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- ═══════════════════ TOAST ═══════════════════ -->
      @if (toastMessage()) {
        <div
          class="fixed bottom-6 right-6 z-[100] rounded-xl bg-zinc-900 border border-primary/50 shadow-xl shadow-primary/10 px-5 py-3.5 text-sm text-white flex items-center gap-3"
        >
          <span
            class="w-5 h-5 rounded-full bg-primary flex items-center justify-center text-black text-xs font-black"
            >✓</span
          >
          {{ toastMessage() }}
        </div>
      }
    </div>
  `,
})
export class ShopComponent implements OnInit {
  fashionService = inject(FashionService);
  router = inject(Router);
  products = signal<Product[]>([]);
  selectedProduct = signal<Product | null>(null);
  selectedSize = signal<string>("");
  filters = { category: "", size: "", color: "" };
  toastMessage = signal<string | null>(null);
  isLoading = signal(true);

  stats = [
    { value: "12K+", label: "Products" },
    { value: "240+", label: "Brands" },
    { value: "98%", label: "Satisfied" },
  ];

  categories = [
    { value: "", label: "All" },
    { value: "SHIRT", label: "Shirts" },
    { value: "PANTS", label: "Pants" },
    { value: "SHOES", label: "Shoes" },
    { value: "ACCESSORY", label: "Accessories" },
    { value: "OUTERWEAR", label: "Outerwear" },
    { value: "DRESS", label: "Dresses" },
    { value: "ACTIVEWEAR", label: "Activewear" },
  ];

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.isLoading.set(true);
    this.fashionService.getProducts(this.filters).subscribe({
      next: (res) => {
        this.products.set(res.content);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  setCategory(value: string) {
    this.filters.category = value;
    this.loadProducts();
  }

  setSize(value: string) {
    this.filters.size = value;
    this.loadProducts();
  }

  clearFilters() {
    this.filters = { category: "", size: "", color: "" };
    this.loadProducts();
  }

  getCategoryLabel(value: string) {
    return this.categories.find((c) => c.value === value)?.label || value;
  }

  getProductImage(product: Product): string {
    return product.imageUrls?.length > 0
      ? product.imageUrls[0]
      : `https://placehold.co/400x533/1a1a1a/D4AF37?text=${encodeURIComponent(product.name)}`;
  }

  getDisplaySizes(product: Product): string[] {
    return (product.availableSizes || []).slice(0, 3);
  }

  openDetail(product: Product) {
    this.selectedProduct.set(product);
    this.selectedSize.set("");
    document.body.style.overflow = "hidden";
  }

  closeDetail() {
    this.selectedProduct.set(null);
    document.body.style.overflow = "";
  }

  scrollToGrid() {
    document
      .getElementById("product-grid")
      ?.scrollIntoView({ behavior: "smooth" });
  }

  addToCart(product: Product, event: Event) {
    event.stopPropagation();
    event.preventDefault();
    this.fashionService.addToCart(product.id).subscribe({
      next: () => {
        this.toastMessage.set(`${product.name} added to cart!`);
        setTimeout(() => this.toastMessage.set(null), 2500);
        if (this.selectedProduct()) this.closeDetail();
      },
      error: () => {
        this.toastMessage.set("Please log in to add items to cart.");
        setTimeout(() => this.toastMessage.set(null), 2500);
      },
    });
  }
}
