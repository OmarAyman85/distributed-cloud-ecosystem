import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  brand: string;
  imageUrls: string[];
  availableSizes?: string[];
  colors?: string[];
}

export interface Cart {
  items: CartItem[];
  totalAmount: number;
}

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
}

export interface WardrobeItem {
  id: number;
  product: Product;
  status: "OWNED" | "WISHLIST";
}

export interface FashionProfile {
  id: string;
  userId: string;
  avatarConfig: AvatarConfig;
}

export interface AvatarConfig {
  skinTone: string;
  hairStyle: string;
  hairColor: string;
  eyeColor: string;
  faceShape: string;
  noseStyle?: string;
  mouthStyle?: string;
  accessory?: string;
  equippedTopId?: number;
  equippedBottomId?: number;
  equippedShoeId?: number;
  equippedOuterwearId?: number;
}

@Injectable({
  providedIn: "root",
})
export class FashionService {
  private http = inject(HttpClient);
  private apiUrl = "http://localhost:8080/api/fashion";

  // Products
  getProducts(filters: any = {}): Observable<{ content: Product[] }> {
    let params = new HttpParams();
    Object.keys(filters).forEach((key) => {
      if (filters[key]) params = params.set(key, filters[key]);
    });
    return this.http.get<{ content: Product[] }>(`${this.apiUrl}/products`, {
      params,
    });
  }

  // Cart
  getCart(): Observable<Cart> {
    return this.http.get<Cart>(`${this.apiUrl}/cart`);
  }

  addToCart(productId: number, quantity: number = 1): Observable<Cart> {
    const params = new HttpParams()
      .set("productId", productId)
      .set("quantity", quantity);
    return this.http.post<Cart>(`${this.apiUrl}/cart/add`, {}, { params });
  }

  removeFromCart(productId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/cart/remove/${productId}`);
  }

  checkout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/orders/checkout`, {});
  }

  // Wardrobe
  getWardrobe(): Observable<WardrobeItem[]> {
    return this.http.get<WardrobeItem[]>(
      `${this.apiUrl}/wardrobe?status=OWNED`,
    );
  }

  // Profile
  getProfile(): Observable<FashionProfile> {
    return this.http.get<FashionProfile>(`${this.apiUrl}/profile`);
  }

  updateProfile(data: any): Observable<FashionProfile> {
    return this.http.put<FashionProfile>(`${this.apiUrl}/profile`, data);
  }
}
