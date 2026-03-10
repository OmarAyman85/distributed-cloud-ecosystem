import { Routes } from "@angular/router";

import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { ShopComponent } from "./pages/shop/shop.component";
import { CartComponent } from "./pages/cart/cart.component";
import { WardrobeComponent } from "./pages/wardrobe/wardrobe.component";
import { MimojiComponent } from "./pages/mimoji/mimoji.component";

export const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "shop", component: ShopComponent },
  { path: "cart", component: CartComponent },
  { path: "wardrobe", component: WardrobeComponent },
  { path: "mimoji", component: MimojiComponent },
  { path: "", redirectTo: "shop", pathMatch: "full" },
];
