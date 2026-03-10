import { Component, inject, signal, computed, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FashionService,
  Product,
  WardrobeItem,
} from "../../services/fashion.service";
import { NavbarComponent } from "../../components/navbar/navbar.component";

// ─── Color & Option Maps ─────────────────────────────────────────────────────

const SKIN: Record<string, string> = {
  Porcelain: "#FDDBB4",
  Fair: "#F1C27D",
  Light: "#E8A87C",
  Medium: "#C68642",
  Olive: "#A0785A",
  Tan: "#8D5524",
  Brown: "#6B3A2A",
  Dark: "#4A2311",
};
const HAIR_COL: Record<string, string> = {
  Midnight: "#0D0D0D",
  Espresso: "#3B1F0E",
  Chestnut: "#6B3A2A",
  Auburn: "#922B21",
  Golden: "#CCA300",
  Platinum: "#E8E0D0",
  Silver: "#9BA4AF",
  Pink: "#D63384",
  Blue: "#0D6EFD",
};
const EYE_COL: Record<string, string> = {
  Onyx: "#1a1a1a",
  Espresso: "#3E2000",
  Amber: "#B5651D",
  Hazel: "#7B5C3A",
  Jade: "#2D6A4F",
  Sapphire: "#1B4F8A",
  Slate: "#6B7280",
  Violet: "#7C3AED",
};

// Outfit colour palettes per type
const TOP_COLORS: Record<
  string,
  { fill: string; shadow: string; label: string }
> = {
  White: { fill: "#f5f0e8", shadow: "#c8c0b0", label: "White" },
  Black: { fill: "#1a1a1a", shadow: "#000000", label: "Black" },
  Navy: { fill: "#1B2A4A", shadow: "#0d1a30", label: "Navy" },
  Grey: { fill: "#5a5a6e", shadow: "#3a3a4a", label: "Grey" },
  Gold: { fill: "#D4AF37", shadow: "#a88000", label: "Gold" },
  Crimson: { fill: "#8B0000", shadow: "#5a0000", label: "Crimson" },
  Olive: { fill: "#4A5240", shadow: "#2a3020", label: "Olive" },
  Sky: { fill: "#4da6d0", shadow: "#2a7aA0", label: "Sky Blue" },
};
const BOTTOM_COLORS: Record<
  string,
  { fill: string; shadow: string; label: string }
> = {
  "Dark Denim": { fill: "#1C2D4A", shadow: "#0c1828", label: "Dark Denim" },
  Black: { fill: "#111111", shadow: "#000000", label: "Black" },
  Khaki: { fill: "#9B8E6E", shadow: "#7a6d4e", label: "Khaki" },
  Grey: { fill: "#4a4a5a", shadow: "#2a2a38", label: "Grey" },
  White: { fill: "#e8e0d4", shadow: "#c0b8a8", label: "White" },
  Olive: { fill: "#4A5240", shadow: "#2a3020", label: "Olive" },
  Charcoal: { fill: "#2c2c3a", shadow: "#0c0c18", label: "Charcoal" },
};
const SHOE_COLORS: Record<
  string,
  { fill: string; sole: string; label: string }
> = {
  "White Sneakers": {
    fill: "#f0ece4",
    sole: "#d0ccc4",
    label: "White Sneakers",
  },
  "Black Leather": { fill: "#1a1a1a", sole: "#3a3a3a", label: "Black Leather" },
  "Brown Oxfords": { fill: "#5C3011", sole: "#3a1e08", label: "Brown Oxfords" },
  "Gold Boots": { fill: "#C8960C", sole: "#8a6600", label: "Gold Boots" },
  "Grey Runners": { fill: "#6a6a7a", sole: "#3a3a4a", label: "Grey Runners" },
  "Red High-Top": { fill: "#8B0000", sole: "#222222", label: "Red Hi-Top" },
};

type HairStyle =
  | "Fade"
  | "Buzz"
  | "Curly"
  | "Wavy"
  | "Long"
  | "Bob"
  | "Bun"
  | "Bald"
  | "Locs"
  | "Mohawk";
type FaceShape = "Oval" | "Round" | "Square" | "Heart";
type Mouth = "Smile" | "Neutral" | "Smirk" | "Grin";
type Nose = "Button" | "Straight" | "Wide";
type AccessoryT = "None" | "Glasses" | "Sunglasses" | "Earrings" | "Chain";
type TopStyle =
  | "Tee"
  | "Shirt"
  | "Hoodie"
  | "Turtleneck"
  | "Tank"
  | "Blazer"
  | "None";
type BottomStyle = "Jeans" | "Chinos" | "Shorts" | "Joggers" | "Skirt" | "None";
type ShoeStyle = "Sneakers" | "Dress Shoes" | "Boots" | "High Tops" | "None";
type OuterwearStyle = "None" | "Bomber" | "Denim Jacket" | "Trenchcoat";

interface AvatarCfg {
  skin: string;
  hairStyle: HairStyle;
  hairColor: string;
  eyeColor: string;
  faceShape: FaceShape;
  nose: Nose;
  mouth: Mouth;
  accessory: AccessoryT;
  topStyle: TopStyle;
  topColor: string;
  bottomStyle: BottomStyle;
  bottomColor: string;
  shoeStyle: ShoeStyle;
  shoeColor: string;
  outerwear: OuterwearStyle;
  equippedTopId?: number;
  equippedBottomId?: number;
  equippedShoeId?: number;
  equippedOuterwearId?: number;
}

@Component({
  selector: "app-mimoji",
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  styles: [
    `
      :host {
        display: block;
      }
      .swatch {
        transition:
          transform 0.15s,
          box-shadow 0.15s;
        cursor: pointer;
      }
      .swatch:hover {
        transform: scale(1.14);
      }
      .swatch.sel {
        box-shadow: 0 0 0 3px #d4af37;
        transform: scale(1.14);
      }
      .opt {
        transition: all 0.18s ease;
        cursor: pointer;
      }
      .opt:hover {
        transform: scale(1.03);
      }
      .opt.sel {
        border-color: #d4af37 !important;
        color: #d4af37 !important;
        background: #1a1500 !important;
      }
      .tab {
        transition: all 0.2s ease;
        cursor: pointer;
      }
      .glow {
        animation: glowPulse 3s ease infinite;
      }
      @keyframes glowPulse {
        0%,
        100% {
          filter: drop-shadow(0 0 12px rgba(212, 175, 55, 0.15));
        }
        50% {
          filter: drop-shadow(0 0 28px rgba(212, 175, 55, 0.35));
        }
      }
    `,
  ],
  template: `
    <div class="min-h-screen bg-zinc-950 text-white">
      <app-navbar />

      <div class="mx-auto max-w-7xl px-4 py-8">
        <div class="mb-6">
          <h1 class="text-3xl font-black">Mimoji · Outfit Creator</h1>
          <p class="text-zinc-500 text-sm mt-1">
            Build your full fashion identity — face, hair, and complete outfit.
          </p>
        </div>

        <div
          class="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-8 items-start"
        >
          <!-- ══════ AVATAR CANVAS ══════ -->
          <div class="flex flex-col items-center gap-5 lg:sticky lg:top-24">
            <div class="relative w-56 flex items-center justify-center">
              <svg
                class="glow w-full"
                viewBox="0 0 160 360"
                xmlns="http://www.w3.org/2000/svg"
              >
                <defs>
                  <radialGradient id="sg" cx="50%" cy="35%" r="65%">
                    <stop offset="0%" [attr.stop-color]="lt(cfg().skin, 18)" />
                    <stop offset="100%" [attr.stop-color]="SKIN[cfg().skin]" />
                  </radialGradient>
                  <radialGradient id="eg" cx="30%" cy="30%" r="70%">
                    <stop
                      offset="0%"
                      [attr.stop-color]="
                        lt(EYE_COL[cfg().eyeColor] ?? '#333', 40)
                      "
                    />
                    <stop
                      offset="100%"
                      [attr.stop-color]="EYE_COL[cfg().eyeColor] ?? '#333'"
                    />
                  </radialGradient>
                </defs>

                <!-- ── OUTERWEAR (back layer) ── -->
                <ng-container [ngSwitch]="cfg().outerwear">
                  <ng-template ngSwitchCase="Bomber">
                    <rect
                      x="14"
                      y="138"
                      width="132"
                      height="80"
                      rx="8"
                      fill="#2a2a2a"
                    />
                    <rect
                      x="14"
                      y="138"
                      width="12"
                      height="80"
                      rx="4"
                      fill="#1a1a1a"
                    />
                    <rect
                      x="134"
                      y="138"
                      width="12"
                      height="80"
                      rx="4"
                      fill="#1a1a1a"
                    />
                    <rect
                      x="26"
                      y="138"
                      width="108"
                      height="8"
                      rx="3"
                      fill="#D4AF37"
                      opacity=".6"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Denim Jacket">
                    <rect
                      x="12"
                      y="136"
                      width="136"
                      height="85"
                      rx="6"
                      fill="#1C2D4A"
                    />
                    <rect
                      x="12"
                      y="136"
                      width="14"
                      height="85"
                      rx="4"
                      fill="#162238"
                    />
                    <rect
                      x="134"
                      y="136"
                      width="14"
                      height="85"
                      rx="4"
                      fill="#162238"
                    />
                    <line
                      x1="80"
                      y1="140"
                      x2="80"
                      y2="220"
                      stroke="#2a3a5a"
                      stroke-width="2"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Trenchcoat">
                    <rect
                      x="10"
                      y="135"
                      width="140"
                      height="120"
                      rx="6"
                      fill="#5C4A32"
                    />
                    <rect
                      x="10"
                      y="135"
                      width="15"
                      height="120"
                      rx="4"
                      fill="#4a3a24"
                    />
                    <rect
                      x="135"
                      y="135"
                      width="15"
                      height="120"
                      rx="4"
                      fill="#4a3a24"
                    />
                    <rect
                      x="25"
                      y="145"
                      width="4"
                      height="80"
                      rx="2"
                      fill="#D4AF37"
                      opacity=".5"
                    />
                  </ng-template>
                </ng-container>

                <!-- ── BODY / NECK ── -->
                <rect
                  x="64"
                  y="96"
                  width="32"
                  height="28"
                  rx="8"
                  fill="url(#sg)"
                />

                <!-- ── TOP ── -->
                @if (cfg().topStyle !== "None") {
                  <ng-container [ngSwitch]="cfg().topStyle">
                    <!-- Tee -->
                    <ng-template ngSwitchCase="Tee">
                      <path
                        [attr.d]="
                          'M28,138 L52,118 L108,118 L132,138 L132,220 L28,220 Z'
                        "
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M52,118 Q80,108 108,118 L108,138 Q80,128 52,138 Z"
                        [attr.fill]="topShadow()"
                      />
                      <!-- sleeves -->
                      <path
                        d="M28,138 L14,148 L14,185 L28,178 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M132,138 L146,148 L146,185 L132,178 Z"
                        [attr.fill]="topFill()"
                      />
                    </ng-template>
                    <!-- Shirt (collar) -->
                    <ng-template ngSwitchCase="Shirt">
                      <path
                        d="M28,138 L52,116 L108,116 L132,138 L132,222 L28,222 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M52,116 Q80,108 108,116 L108,135 Q80,126 52,135 Z"
                        [attr.fill]="topShadow()"
                      />
                      <path
                        d="M28,138 L14,145 L14,188 L28,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M132,138 L146,145 L146,188 L132,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <!-- collar -->
                      <path
                        d="M70,116 L80,132 L90,116"
                        fill="white"
                        opacity=".9"
                      />
                      <line
                        x1="80"
                        y1="132"
                        x2="80"
                        y2="222"
                        stroke="white"
                        stroke-width="1"
                        opacity=".3"
                      />
                      <!-- buttons -->
                      @for (b of [148, 162, 176, 190]; track b) {
                        <circle
                          cx="80"
                          [attr.cy]="b"
                          r="2.5"
                          fill="white"
                          opacity=".6"
                        />
                      }
                    </ng-template>
                    <!-- Hoodie -->
                    <ng-template ngSwitchCase="Hoodie">
                      <path
                        d="M26,138 L50,118 L110,118 L134,138 L134,222 L26,222 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M26,138 L14,145 L14,190 L26,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M134,138 L146,145 L146,190 L134,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <!-- hood shape -->
                      <path
                        d="M54,118 Q80,104 106,118 L102,130 Q80,120 58,130 Z"
                        [attr.fill]="topShadow()"
                      />
                      <!-- pocket -->
                      <rect
                        x="60"
                        y="185"
                        width="40"
                        height="22"
                        rx="6"
                        [attr.fill]="topShadow()"
                      />
                      <line
                        x1="80"
                        y1="185"
                        x2="80"
                        y2="207"
                        stroke="#0005"
                        stroke-width="1"
                      />
                    </ng-template>
                    <!-- Turtleneck -->
                    <ng-template ngSwitchCase="Turtleneck">
                      <path
                        d="M28,140 L52,118 L108,118 L132,140 L132,222 L28,222 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M28,140 L14,148 L14,188 L28,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M132,140 L146,148 L146,188 L132,180 Z"
                        [attr.fill]="topFill()"
                      />
                      <!-- neck tube -->
                      <rect
                        x="58"
                        y="96"
                        width="44"
                        height="30"
                        rx="10"
                        [attr.fill]="topFill()"
                      />
                    </ng-template>
                    <!-- Tank -->
                    <ng-template ngSwitchCase="Tank">
                      <path
                        d="M44,135 L56,118 L104,118 L116,135 L116,222 L44,222 Z"
                        [attr.fill]="topFill()"
                      />
                      <!-- straps -->
                      <rect
                        x="56"
                        y="104"
                        width="14"
                        height="32"
                        rx="4"
                        [attr.fill]="topFill()"
                      />
                      <rect
                        x="90"
                        y="104"
                        width="14"
                        height="32"
                        rx="4"
                        [attr.fill]="topFill()"
                      />
                      <!-- arms shown as skin -->
                      <rect
                        x="14"
                        y="140"
                        width="28"
                        height="55"
                        rx="10"
                        fill="url(#sg)"
                      />
                      <rect
                        x="118"
                        y="140"
                        width="28"
                        height="55"
                        rx="10"
                        fill="url(#sg)"
                      />
                    </ng-template>
                    <!-- Blazer -->
                    <ng-template ngSwitchCase="Blazer">
                      <path
                        d="M26,138 L50,116 L110,116 L134,138 L134,222 L26,222 Z"
                        [attr.fill]="topFill()"
                      />
                      <path
                        d="M26,138 L10,144 L10,192 L26,180 Z"
                        [attr.fill]="topShadow()"
                      />
                      <path
                        d="M134,138 L150,144 L150,192 L134,180 Z"
                        [attr.fill]="topShadow()"
                      />
                      <!-- lapels -->
                      <path
                        d="M70,116 L50,144 L80,152 Z"
                        fill="white"
                        opacity=".15"
                      />
                      <path
                        d="M90,116 L110,144 L80,152 Z"
                        fill="white"
                        opacity=".15"
                      />
                      <!-- pocket square -->
                      <rect
                        x="38"
                        y="156"
                        width="12"
                        height="8"
                        rx="2"
                        fill="white"
                        opacity=".5"
                      />
                      <!-- buttons -->
                      @for (b of [162, 178]; track b) {
                        <circle
                          cx="80"
                          [attr.cy]="b"
                          r="3.5"
                          [attr.fill]="topShadow()"
                          stroke="white"
                          stroke-width="1"
                          opacity=".5"
                        />
                      }
                    </ng-template>
                  </ng-container>
                } @else {
                  <!-- No top → show torso skin -->
                  <rect
                    x="28"
                    y="118"
                    width="104"
                    height="104"
                    rx="6"
                    fill="url(#sg)"
                  />
                  <rect
                    x="14"
                    y="140"
                    width="16"
                    height="55"
                    rx="8"
                    fill="url(#sg)"
                  />
                  <rect
                    x="130"
                    y="140"
                    width="16"
                    height="55"
                    rx="8"
                    fill="url(#sg)"
                  />
                }

                <!-- ── BOTTOM ── -->
                @if (cfg().bottomStyle !== "None") {
                  <ng-container [ngSwitch]="cfg().bottomStyle">
                    <ng-template ngSwitchCase="Jeans">
                      <rect
                        x="28"
                        y="218"
                        width="48"
                        height="90"
                        rx="6"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="84"
                        y="218"
                        width="48"
                        height="90"
                        rx="6"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="28"
                        y="218"
                        width="104"
                        height="18"
                        rx="3"
                        [attr.fill]="bottomShadow()"
                      />
                      <!-- seam lines -->
                      <line
                        x1="52"
                        y1="236"
                        x2="52"
                        y2="308"
                        stroke="#0004"
                        stroke-width="1"
                      />
                      <line
                        x1="108"
                        y1="236"
                        x2="108"
                        y2="308"
                        stroke="#0004"
                        stroke-width="1"
                      />
                      <!-- belt loops -->
                      @for (bx of [36, 60, 84, 100, 120]; track bx) {
                        <rect
                          [attr.x]="bx"
                          y="218"
                          width="4"
                          height="10"
                          rx="1"
                          [attr.fill]="bottomShadow()"
                        />
                      }
                    </ng-template>
                    <ng-template ngSwitchCase="Chinos">
                      <rect
                        x="30"
                        y="220"
                        width="46"
                        height="86"
                        rx="8"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="84"
                        y="220"
                        width="46"
                        height="86"
                        rx="8"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="30"
                        y="220"
                        width="100"
                        height="14"
                        rx="3"
                        [attr.fill]="bottomShadow()"
                      />
                    </ng-template>
                    <ng-template ngSwitchCase="Shorts">
                      <rect
                        x="30"
                        y="220"
                        width="46"
                        height="50"
                        rx="8"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="84"
                        y="220"
                        width="46"
                        height="50"
                        rx="8"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="30"
                        y="220"
                        width="100"
                        height="14"
                        rx="3"
                        [attr.fill]="bottomShadow()"
                      />
                      <!-- legs visible below -->
                      <rect
                        x="30"
                        y="270"
                        width="46"
                        height="38"
                        rx="6"
                        fill="url(#sg)"
                      />
                      <rect
                        x="84"
                        y="270"
                        width="46"
                        height="38"
                        rx="6"
                        fill="url(#sg)"
                      />
                    </ng-template>
                    <ng-template ngSwitchCase="Joggers">
                      <rect
                        x="28"
                        y="218"
                        width="48"
                        height="90"
                        rx="12"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="84"
                        y="218"
                        width="48"
                        height="90"
                        rx="12"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="28"
                        y="218"
                        width="104"
                        height="16"
                        rx="4"
                        [attr.fill]="bottomShadow()"
                      />
                      <!-- elastic cuffs -->
                      <rect
                        x="28"
                        y="296"
                        width="48"
                        height="12"
                        rx="6"
                        [attr.fill]="bottomShadow()"
                      />
                      <rect
                        x="84"
                        y="296"
                        width="48"
                        height="12"
                        rx="6"
                        [attr.fill]="bottomShadow()"
                      />
                    </ng-template>
                    <ng-template ngSwitchCase="Skirt">
                      <path
                        d="M28,220 L20,308 L140,308 L132,220 Z"
                        [attr.fill]="bottomFill()"
                      />
                      <rect
                        x="28"
                        y="218"
                        width="104"
                        height="14"
                        rx="3"
                        [attr.fill]="bottomShadow()"
                      />
                      <!-- pleats -->
                      @for (px of [50, 70, 90, 110]; track px) {
                        <line
                          [attr.x1]="px"
                          y1="232"
                          [attr.x2]="px - 4"
                          y2="308"
                          stroke="#0003"
                          stroke-width="1"
                        />
                      }
                    </ng-template>
                  </ng-container>
                } @else {
                  <!-- No bottom: bare legs -->
                  <rect
                    x="30"
                    y="220"
                    width="46"
                    height="88"
                    rx="8"
                    fill="url(#sg)"
                  />
                  <rect
                    x="84"
                    y="220"
                    width="46"
                    height="88"
                    rx="8"
                    fill="url(#sg)"
                  />
                }

                <!-- ── SHOES ── -->
                @if (cfg().shoeStyle !== "None") {
                  <ng-container [ngSwitch]="cfg().shoeStyle">
                    <ng-template ngSwitchCase="Sneakers">
                      <rect
                        x="18"
                        y="304"
                        width="56"
                        height="20"
                        rx="8"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="86"
                        y="304"
                        width="56"
                        height="20"
                        rx="8"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="18"
                        y="316"
                        width="56"
                        height="8"
                        rx="4"
                        [attr.fill]="shoeSole()"
                      />
                      <rect
                        x="86"
                        y="316"
                        width="56"
                        height="8"
                        rx="4"
                        [attr.fill]="shoeSole()"
                      />
                      <!-- lace lines -->
                      @for (lx of [26, 34, 42, 50, 58]; track lx) {
                        <line
                          [attr.x1]="lx"
                          y1="308"
                          [attr.x2]="lx"
                          y2="316"
                          stroke="white"
                          stroke-width="1"
                          opacity=".5"
                        />
                      }
                      @for (lx of [94, 102, 110, 118, 126]; track lx) {
                        <line
                          [attr.x1]="lx"
                          y1="308"
                          [attr.x2]="lx"
                          y2="316"
                          stroke="white"
                          stroke-width="1"
                          opacity=".5"
                        />
                      }
                    </ng-template>
                    <ng-template ngSwitchCase="Dress Shoes">
                      <ellipse
                        cx="46"
                        cy="316"
                        rx="30"
                        ry="12"
                        [attr.fill]="shoeFill()"
                      />
                      <ellipse
                        cx="114"
                        cy="316"
                        rx="30"
                        ry="12"
                        [attr.fill]="shoeFill()"
                      />
                      <!-- highlight -->
                      <ellipse
                        cx="38"
                        cy="312"
                        rx="12"
                        ry="5"
                        fill="white"
                        opacity=".12"
                      />
                      <ellipse
                        cx="106"
                        cy="312"
                        rx="12"
                        ry="5"
                        fill="white"
                        opacity=".12"
                      />
                    </ng-template>
                    <ng-template ngSwitchCase="Boots">
                      <rect
                        x="20"
                        y="294"
                        width="52"
                        height="34"
                        rx="6"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="88"
                        y="294"
                        width="52"
                        height="34"
                        rx="6"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="20"
                        y="318"
                        width="52"
                        height="10"
                        rx="4"
                        [attr.fill]="shoeSole()"
                      />
                      <rect
                        x="88"
                        y="318"
                        width="52"
                        height="10"
                        rx="4"
                        [attr.fill]="shoeSole()"
                      />
                      <!-- boot stitching -->
                      <line
                        x1="22"
                        y1="305"
                        x2="70"
                        y2="305"
                        stroke="white"
                        stroke-width="0.7"
                        opacity=".3"
                        stroke-dasharray="2,2"
                      />
                      <line
                        x1="90"
                        y1="305"
                        x2="138"
                        y2="305"
                        stroke="white"
                        stroke-width="0.7"
                        opacity=".3"
                        stroke-dasharray="2,2"
                      />
                    </ng-template>
                    <ng-template ngSwitchCase="High Tops">
                      <rect
                        x="18"
                        y="290"
                        width="56"
                        height="38"
                        rx="8"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="86"
                        y="290"
                        width="56"
                        height="38"
                        rx="8"
                        [attr.fill]="shoeFill()"
                      />
                      <rect
                        x="18"
                        y="318"
                        width="56"
                        height="10"
                        rx="5"
                        [attr.fill]="shoeSole()"
                      />
                      <rect
                        x="86"
                        y="318"
                        width="56"
                        height="10"
                        rx="5"
                        [attr.fill]="shoeSole()"
                      />
                      <!-- tongue + laces -->
                      <rect
                        x="34"
                        y="288"
                        width="24"
                        height="30"
                        rx="4"
                        [attr.fill]="lt(shoeFill(), 20)"
                      />
                      <rect
                        x="102"
                        y="288"
                        width="24"
                        height="30"
                        rx="4"
                        [attr.fill]="lt(shoeFill(), 20)"
                      />
                      @for (ly of [296, 303, 310]; track ly) {
                        <line
                          [attr.x1]="28"
                          [attr.y1]="ly"
                          x2="34"
                          [attr.y2]="ly"
                          stroke="white"
                          stroke-width="1.5"
                          opacity=".5"
                        />
                        <line
                          [attr.x1]="58"
                          [attr.y1]="ly"
                          x2="64"
                          [attr.y2]="ly"
                          stroke="white"
                          stroke-width="1.5"
                          opacity=".5"
                        />
                        <line
                          [attr.x1]="96"
                          [attr.y1]="ly"
                          x2="102"
                          [attr.y2]="ly"
                          stroke="white"
                          stroke-width="1.5"
                          opacity=".5"
                        />
                        <line
                          [attr.x1]="126"
                          [attr.y1]="ly"
                          x2="132"
                          [attr.y2]="ly"
                          stroke="white"
                          stroke-width="1.5"
                          opacity=".5"
                        />
                      }
                    </ng-template>
                  </ng-container>
                }

                <!-- ══ FACE HEAD ══ -->
                <!-- hair back -->
                <ng-container [ngSwitch]="cfg().hairStyle">
                  <ng-template ngSwitchCase="Long">
                    <rect
                      x="33"
                      y="26"
                      width="94"
                      height="120"
                      rx="10"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Locs">
                    @for (lx of [37, 48, 59, 70, 81, 91, 102, 112]; track lx) {
                      <rect
                        [attr.x]="lx"
                        y="32"
                        width="7"
                        height="72"
                        rx="3"
                        [attr.fill]="hairFill()"
                        opacity=".9"
                      />
                    }
                  </ng-template>
                </ng-container>

                <!-- face shape -->
                <ellipse
                  [attr.rx]="faceRx()"
                  [attr.ry]="faceRy()"
                  cx="80"
                  cy="65"
                  fill="url(#sg)"
                />
                <!-- ears -->
                <ellipse
                  cx="36"
                  cy="68"
                  rx="7"
                  ry="10"
                  [attr.fill]="SKIN[cfg().skin]"
                />
                <ellipse
                  cx="124"
                  cy="68"
                  rx="7"
                  ry="10"
                  [attr.fill]="SKIN[cfg().skin]"
                />
                <!-- ear canal -->
                <ellipse
                  cx="36"
                  cy="68"
                  rx="3.5"
                  ry="5.5"
                  [attr.fill]="dk(cfg().skin, 10)"
                />
                <ellipse
                  cx="124"
                  cy="68"
                  rx="3.5"
                  ry="5.5"
                  [attr.fill]="dk(cfg().skin, 10)"
                />

                <!-- earrings -->
                @if (cfg().accessory === "Earrings") {
                  <circle cx="30" cy="77" r="4" fill="#D4AF37" />
                  <circle cx="130" cy="77" r="4" fill="#D4AF37" />
                }
                <!-- chain -->
                @if (cfg().accessory === "Chain") {
                  <path
                    d="M56,104 Q80,115 104,104"
                    stroke="#D4AF37"
                    stroke-width="2.5"
                    fill="none"
                    opacity=".9"
                  />
                  <circle cx="80" cy="113" r="4" fill="#D4AF37" />
                }

                <!-- hair front -->
                <ng-container [ngSwitch]="cfg().hairStyle">
                  <ng-template ngSwitchCase="Fade">
                    <ellipse
                      cx="80"
                      cy="38"
                      rx="38"
                      ry="22"
                      [attr.fill]="hairFill()"
                    />
                    <rect
                      x="42"
                      y="38"
                      width="76"
                      height="12"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Buzz">
                    <ellipse
                      cx="80"
                      cy="40"
                      rx="36"
                      ry="18"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Curly">
                    @for (c of curly; track c.cx) {
                      <circle
                        [attr.cx]="c.cx"
                        [attr.cy]="c.cy"
                        [attr.r]="c.r"
                        [attr.fill]="hairFill()"
                      />
                    }
                  </ng-template>
                  <ng-template ngSwitchCase="Wavy">
                    <path
                      d="M42,46 Q52,30 62,43 Q72,30 82,43 Q92,30 102,43 Q112,30 122,43 L122,26 Q112,18 102,26 Q92,18 82,26 Q72,18 62,26 Q52,18 42,26 Z"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Long">
                    <ellipse
                      cx="80"
                      cy="38"
                      rx="38"
                      ry="20"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Bob">
                    <ellipse
                      cx="80"
                      cy="38"
                      rx="38"
                      ry="20"
                      [attr.fill]="hairFill()"
                    />
                    <rect
                      x="42"
                      y="50"
                      width="76"
                      height="26"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Bun">
                    <ellipse
                      cx="80"
                      cy="42"
                      rx="36"
                      ry="16"
                      [attr.fill]="hairFill()"
                    />
                    <circle cx="80" cy="20" r="16" [attr.fill]="hairFill()" />
                    <circle
                      cx="80"
                      cy="13"
                      r="9"
                      [attr.fill]="dk(cfg().hairColor, 12)"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Locs">
                    <ellipse
                      cx="80"
                      cy="38"
                      rx="38"
                      ry="20"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Mohawk">
                    <rect
                      x="68"
                      y="10"
                      width="24"
                      height="36"
                      rx="8"
                      [attr.fill]="hairFill()"
                    />
                    <rect
                      x="65"
                      y="38"
                      width="30"
                      height="14"
                      [attr.fill]="hairFill()"
                    />
                  </ng-template>
                  <ng-template ngSwitchCase="Bald"><!-- none --></ng-template>
                </ng-container>

                <!-- eyebrows -->
                <path
                  [attr.d]="'M54,53 Q63,48 72,52'"
                  [attr.stroke]="dk(cfg().hairColor, 15)"
                  stroke-width="2.5"
                  fill="none"
                  stroke-linecap="round"
                />
                <path
                  [attr.d]="'M88,52 Q97,48 106,53'"
                  [attr.stroke]="dk(cfg().hairColor, 15)"
                  stroke-width="2.5"
                  fill="none"
                  stroke-linecap="round"
                />

                <!-- eyes -->
                <ellipse cx="63" cy="66" rx="8" ry="7" fill="white" />
                <circle cx="63" cy="67" r="5" fill="url(#eg)" />
                <circle cx="63" cy="67" r="2.5" fill="#050505" />
                <circle cx="65" cy="65" r="1.2" fill="white" opacity=".85" />
                <ellipse cx="97" cy="66" rx="8" ry="7" fill="white" />
                <circle cx="97" cy="67" r="5" fill="url(#eg)" />
                <circle cx="97" cy="67" r="2.5" fill="#050505" />
                <circle cx="99" cy="65" r="1.2" fill="white" opacity=".85" />
                <!-- eyelid lines -->
                <path
                  d="M55,66 Q63,60 71,66"
                  stroke="#2224"
                  stroke-width="1"
                  fill="none"
                />
                <path
                  d="M89,66 Q97,60 105,66"
                  stroke="#2224"
                  stroke-width="1"
                  fill="none"
                />

                <!-- glasses -->
                @if (cfg().accessory === "Glasses") {
                  <rect
                    x="52"
                    y="60"
                    width="22"
                    height="13"
                    rx="5"
                    fill="none"
                    stroke="#D4AF37"
                    stroke-width="2"
                  />
                  <rect
                    x="86"
                    y="60"
                    width="22"
                    height="13"
                    rx="5"
                    fill="none"
                    stroke="#D4AF37"
                    stroke-width="2"
                  />
                  <line
                    x1="74"
                    y1="66"
                    x2="86"
                    y2="66"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                  <line
                    x1="44"
                    y1="64"
                    x2="52"
                    y2="64"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                  <line
                    x1="108"
                    y1="64"
                    x2="116"
                    y2="64"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                }
                @if (cfg().accessory === "Sunglasses") {
                  <rect
                    x="50"
                    y="60"
                    width="24"
                    height="12"
                    rx="4"
                    fill="#111"
                    opacity=".85"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                  <rect
                    x="86"
                    y="60"
                    width="24"
                    height="12"
                    rx="4"
                    fill="#111"
                    opacity=".85"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                  <line
                    x1="44"
                    y1="64"
                    x2="50"
                    y2="64"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                  <line
                    x1="110"
                    y1="64"
                    x2="116"
                    y2="64"
                    stroke="#D4AF37"
                    stroke-width="1.5"
                  />
                }

                <!-- nose -->
                @switch (cfg().nose) {
                  @case ("Button") {
                    <circle
                      cx="80"
                      cy="78"
                      r="2.5"
                      [attr.fill]="dk(cfg().skin, 12)"
                      opacity=".5"
                    />
                  }
                  @case ("Straight") {
                    <path
                      d="M77,72 L77,79 Q80,82 83,79 L83,72"
                      [attr.stroke]="dk(cfg().skin, 18)"
                      stroke-width="1.5"
                      fill="none"
                      stroke-linecap="round"
                    />
                  }
                  @case ("Wide") {
                    <path
                      d="M74,76 Q80,82 86,76"
                      [attr.stroke]="dk(cfg().skin, 18)"
                      stroke-width="2"
                      fill="none"
                      stroke-linecap="round"
                    />
                  }
                }

                <!-- mouth -->
                @switch (cfg().mouth) {
                  @case ("Smile") {
                    <path
                      d="M70,88 Q80,96 90,88"
                      stroke="#C0392B"
                      stroke-width="2"
                      fill="none"
                      stroke-linecap="round"
                    />
                    <path
                      d="M70,88 Q80,98 90,88"
                      fill="#E57373"
                      opacity=".35"
                    />
                  }
                  @case ("Neutral") {
                    <line
                      x1="71"
                      y1="90"
                      x2="89"
                      y2="90"
                      stroke="#C0392B"
                      stroke-width="2"
                      stroke-linecap="round"
                    />
                  }
                  @case ("Smirk") {
                    <path
                      d="M72,90 Q81,95 90,87"
                      stroke="#C0392B"
                      stroke-width="2"
                      fill="none"
                      stroke-linecap="round"
                    />
                  }
                  @case ("Grin") {
                    <path
                      d="M67,87 Q80,97 93,87"
                      stroke="#C0392B"
                      stroke-width="2"
                      fill="none"
                      stroke-linecap="round"
                    />
                    <path
                      d="M67,87 Q80,100 93,87"
                      fill="#E57373"
                      opacity=".3"
                    />
                    <line
                      x1="70"
                      y1="89"
                      x2="90"
                      y2="89"
                      stroke="white"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  }
                }

                <!-- cheeks -->
                <ellipse
                  cx="52"
                  cy="76"
                  rx="8"
                  ry="5"
                  fill="#FF8C7A"
                  opacity=".18"
                />
                <ellipse
                  cx="108"
                  cy="76"
                  rx="8"
                  ry="5"
                  fill="#FF8C7A"
                  opacity=".18"
                />
              </svg>
            </div>

            <!-- Save -->
            @if (msg()) {
              <div
                class="text-sm px-5 py-2 rounded-lg w-full text-center"
                [class.bg-green-900]="msg() === 'ok'"
                [class.text-green-300]="msg() === 'ok'"
                [class.bg-red-900]="msg() === 'err'"
                [class.text-red-300]="msg() === 'err'"
              >
                {{
                  msg() === "ok"
                    ? "✅ Saved!"
                    : "❌ Save failed — try logging in."
                }}
              </div>
            }

            <!-- Currently Wearing Labels -->
            @if (wearingTop() || wearingBottom() || wearingShoe()) {
              <div class="flex flex-col gap-2 w-full">
                <p
                  class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold px-2"
                >
                  Currently Equipped
                </p>
                <div
                  class="bg-zinc-900/50 border border-zinc-800 rounded-xl p-3 space-y-2"
                >
                  @if (wearingTop()) {
                    <div class="flex items-center gap-2 text-[10px]">
                      <span class="text-primary font-black">TOP:</span>
                      <span class="text-zinc-300">{{ wearingTop() }}</span>
                    </div>
                  }
                  @if (wearingBottom()) {
                    <div class="flex items-center gap-2 text-[10px]">
                      <span class="text-primary font-black">BOTTOM:</span>
                      <span class="text-zinc-300">{{ wearingBottom() }}</span>
                    </div>
                  }
                  @if (wearingShoe()) {
                    <div class="flex items-center gap-2 text-[10px]">
                      <span class="text-primary font-black">SHOES:</span>
                      <span class="text-zinc-300">{{ wearingShoe() }}</span>
                    </div>
                  }
                </div>
              </div>
            }

            <button
              (click)="save()"
              class="w-48 bg-primary text-black font-black py-3 rounded-lg text-xs uppercase tracking-widest hover:bg-yellow-400 active:scale-95 transition-all"
            >
              {{ saving() ? "Saving…" : "Save Mimoji" }}
            </button>
          </div>

          <!-- ══════ PANEL ══════ -->
          <div class="space-y-4">
            <!-- Tab bar -->
            <div
              class="flex gap-1 bg-zinc-900 rounded-xl p-1 border border-zinc-800 flex-wrap"
            >
              @for (t of tabs; track t.id) {
                <button
                  (click)="tab.set(t.id)"
                  [class.bg-zinc-800]="tab() === t.id"
                  [class.text-white]="tab() === t.id"
                  [class.text-zinc-500]="tab() !== t.id"
                  class="tab flex-1 min-w-[60px] py-2 text-xs font-bold uppercase tracking-wider rounded-lg text-center"
                >
                  {{ t.icon }} {{ t.label }}
                </button>
              }
            </div>

            <!-- Tab content -->

            <!-- ─ Wardrobe ─ -->
            @if (tab() === "wardrobe") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-6"
              >
                @if (wardrobe().length === 0) {
                  <div class="text-center py-12">
                    <div
                      class="w-16 h-16 bg-zinc-800 rounded-full flex items-center justify-center mx-auto mb-4"
                    >
                      <svg
                        class="w-8 h-8 text-zinc-600"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="1.5"
                          d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
                        />
                      </svg>
                    </div>
                    <p class="text-zinc-400 font-medium">
                      Your wardrobe is empty.
                    </p>
                    <a
                      routerLink="/shop"
                      class="text-primary text-xs font-bold uppercase tracking-widest mt-2 inline-block"
                      >Visit Shop</a
                    >
                  </div>
                } @else {
                  <div class="space-y-6">
                    <!-- Tops -->
                    @if (wardrobeTops().length > 0) {
                      <div>
                        <p
                          class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                        >
                          Tops & Outerwear
                        </p>
                        <div class="grid grid-cols-2 gap-3">
                          @for (item of wardrobeTops(); track item.id) {
                            <button
                              (click)="equip(item)"
                              [class.sel]="
                                cfg().equippedTopId === item.product.id ||
                                cfg().equippedOuterwearId === item.product.id
                              "
                              class="opt group relative flex flex-col items-center p-3 rounded-xl border border-zinc-800 bg-zinc-950/50 hover:border-zinc-700 transition-all"
                            >
                              <img
                                [src]="item.product.imageUrls[0]"
                                class="w-20 h-20 object-contain mb-2 opacity-80 group-hover:opacity-100 transition-opacity"
                              />
                              <span
                                class="text-[10px] font-bold text-zinc-400 text-center truncate w-full"
                                >{{ item.product.name }}</span
                              >
                              @if (
                                cfg().equippedTopId === item.product.id ||
                                cfg().equippedOuterwearId === item.product.id
                              ) {
                                <div
                                  class="absolute top-1 right-1 w-2 h-2 rounded-full bg-primary shadow-[0_0_8px_rgba(212,175,55,0.6)]"
                                ></div>
                              }
                            </button>
                          }
                        </div>
                      </div>
                    }

                    <!-- Bottoms -->
                    @if (wardrobeBottoms().length > 0) {
                      <div>
                        <p
                          class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                        >
                          Bottoms
                        </p>
                        <div class="grid grid-cols-2 gap-3">
                          @for (item of wardrobeBottoms(); track item.id) {
                            <button
                              (click)="equip(item)"
                              [class.sel]="
                                cfg().equippedBottomId === item.product.id
                              "
                              class="opt group relative flex flex-col items-center p-3 rounded-xl border border-zinc-800 bg-zinc-950/50 hover:border-zinc-700 transition-all"
                            >
                              <img
                                [src]="item.product.imageUrls[0]"
                                class="w-20 h-20 object-contain mb-2 opacity-80 group-hover:opacity-100 transition-opacity"
                              />
                              <span
                                class="text-[10px] font-bold text-zinc-400 text-center truncate w-full"
                                >{{ item.product.name }}</span
                              >
                              @if (cfg().equippedBottomId === item.product.id) {
                                <div
                                  class="absolute top-1 right-1 w-2 h-2 rounded-full bg-primary shadow-[0_0_8px_rgba(212,175,55,0.6)]"
                                ></div>
                              }
                            </button>
                          }
                        </div>
                      </div>
                    }

                    <!-- Shoes -->
                    @if (wardrobeShoes().length > 0) {
                      <div>
                        <p
                          class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                        >
                          Shoes
                        </p>
                        <div class="grid grid-cols-2 gap-3">
                          @for (item of wardrobeShoes(); track item.id) {
                            <button
                              (click)="equip(item)"
                              [class.sel]="
                                cfg().equippedShoeId === item.product.id
                              "
                              class="opt group relative flex flex-col items-center p-3 rounded-xl border border-zinc-800 bg-zinc-950/50 hover:border-zinc-700 transition-all"
                            >
                              <img
                                [src]="item.product.imageUrls[0]"
                                class="w-20 h-20 object-contain mb-2 opacity-80 group-hover:opacity-100 transition-opacity"
                              />
                              <span
                                class="text-[10px] font-bold text-zinc-400 text-center truncate w-full"
                                >{{ item.product.name }}</span
                              >
                              @if (cfg().equippedShoeId === item.product.id) {
                                <div
                                  class="absolute top-1 right-1 w-2 h-2 rounded-full bg-primary shadow-[0_0_8px_rgba(212,175,55,0.6)]"
                                ></div>
                              }
                            </button>
                          }
                        </div>
                      </div>
                    }
                  </div>
                }
              </div>
            }

            <!-- ─ Skin ─ -->

            @if (tab() === "skin") {
              <div class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5">
                <p
                  class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-4"
                >
                  Skin Tone
                </p>
                <div class="flex flex-wrap gap-3">
                  @for (e of skinE; track e[0]) {
                    <button
                      (click)="s('skin', e[0])"
                      [class.sel]="cfg().skin === e[0]"
                      class="swatch w-10 h-10 rounded-full border-2 border-zinc-800"
                      [style.background-color]="e[1]"
                      [title]="e[0]"
                    ></button>
                  }
                </div>
              </div>
            }

            <!-- ─ Hair ─ -->
            @if (tab() === "hair") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-5"
              >
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Hair Style
                  </p>
                  <div class="grid grid-cols-4 sm:grid-cols-5 gap-2">
                    @for (h of hairStyles; track h) {
                      <button
                        (click)="s('hairStyle', h)"
                        [class.sel]="cfg().hairStyle === h"
                        class="opt border border-zinc-700 text-zinc-400 rounded-lg py-2 text-xs font-bold text-center"
                      >
                        {{ h }}
                      </button>
                    }
                  </div>
                </div>
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Hair Color
                  </p>
                  <div class="flex flex-wrap gap-3">
                    @for (e of hairE; track e[0]) {
                      <button
                        (click)="s('hairColor', e[0])"
                        [class.sel]="cfg().hairColor === e[0]"
                        class="swatch w-9 h-9 rounded-full border-2 border-zinc-800"
                        [style.background-color]="e[1]"
                        [title]="e[0]"
                      ></button>
                    }
                  </div>
                </div>
              </div>
            }

            <!-- ─ Face ─ -->
            @if (tab() === "face") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-5"
              >
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Eye Color
                  </p>
                  <div class="flex flex-wrap gap-3">
                    @for (e of eyeE; track e[0]) {
                      <button
                        (click)="s('eyeColor', e[0])"
                        [class.sel]="cfg().eyeColor === e[0]"
                        class="swatch w-9 h-9 rounded-full border-2 border-zinc-800"
                        [style.background-color]="e[1]"
                        [title]="e[0]"
                      ></button>
                    }
                  </div>
                </div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <p
                      class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                    >
                      Face Shape
                    </p>
                    <div class="flex flex-wrap gap-2">
                      @for (f of faceShapes; track f) {
                        <button
                          (click)="s('faceShape', f)"
                          [class.sel]="cfg().faceShape === f"
                          class="opt border border-zinc-700 text-zinc-400 px-4 py-2 rounded-lg text-xs font-bold"
                        >
                          {{ f }}
                        </button>
                      }
                    </div>
                  </div>
                  <div>
                    <p
                      class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                    >
                      Nose
                    </p>
                    <div class="flex flex-wrap gap-2">
                      @for (n of noses; track n) {
                        <button
                          (click)="s('nose', n)"
                          [class.sel]="cfg().nose === n"
                          class="opt border border-zinc-700 text-zinc-400 px-4 py-2 rounded-lg text-xs font-bold"
                        >
                          {{ n }}
                        </button>
                      }
                    </div>
                  </div>
                </div>
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Mouth
                  </p>
                  <div class="flex gap-2 flex-wrap">
                    @for (m of mouths; track m) {
                      <button
                        (click)="s('mouth', m)"
                        [class.sel]="cfg().mouth === m"
                        class="opt border border-zinc-700 text-zinc-400 px-5 py-2 rounded-lg text-xs font-bold"
                      >
                        {{ m }}
                      </button>
                    }
                  </div>
                </div>
              </div>
            }

            <!-- ─ Top ─ -->
            @if (tab() === "top") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-5"
              >
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Top Style
                  </p>
                  <div class="grid grid-cols-3 gap-2">
                    @for (t of topStyles; track t) {
                      <button
                        (click)="s('topStyle', t)"
                        [class.sel]="cfg().topStyle === t"
                        class="opt border border-zinc-700 text-zinc-400 py-3 rounded-xl text-xs font-bold"
                      >
                        {{ t }}
                      </button>
                    }
                  </div>
                </div>
                @if (cfg().topStyle !== "None") {
                  <div>
                    <p
                      class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                    >
                      Top Color
                    </p>
                    <div class="grid grid-cols-4 gap-3">
                      @for (e of topColorE; track e[0]) {
                        <button
                          (click)="s('topColor', e[0])"
                          [class.sel]="cfg().topColor === e[0]"
                          class="swatch opt border border-zinc-700 rounded-xl py-4 flex flex-col items-center gap-1.5 text-xs text-zinc-400 font-bold"
                        >
                          <span
                            class="w-6 h-6 rounded-full block"
                            [style.background-color]="e[1].fill"
                          ></span>
                          {{ e[1].label }}
                        </button>
                      }
                    </div>
                  </div>
                }
              </div>
            }

            <!-- ─ Bottom ─ -->
            @if (tab() === "bottom") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-5"
              >
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Bottom Style
                  </p>
                  <div class="grid grid-cols-3 gap-2">
                    @for (b of bottomStyles; track b) {
                      <button
                        (click)="s('bottomStyle', b)"
                        [class.sel]="cfg().bottomStyle === b"
                        class="opt border border-zinc-700 text-zinc-400 py-3 rounded-xl text-xs font-bold"
                      >
                        {{ b }}
                      </button>
                    }
                  </div>
                </div>
                @if (cfg().bottomStyle !== "None") {
                  <div>
                    <p
                      class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                    >
                      Bottom Color
                    </p>
                    <div class="grid grid-cols-4 gap-3">
                      @for (e of bottomColorE; track e[0]) {
                        <button
                          (click)="s('bottomColor', e[0])"
                          [class.sel]="cfg().bottomColor === e[0]"
                          class="swatch opt border border-zinc-700 rounded-xl py-4 flex flex-col items-center gap-1.5 text-xs text-zinc-400 font-bold"
                        >
                          <span
                            class="w-6 h-6 rounded-full block"
                            [style.background-color]="e[1].fill"
                          ></span>
                          {{ e[1].label }}
                        </button>
                      }
                    </div>
                  </div>
                }
              </div>
            }

            <!-- ─ Shoes & Outerwear ─ -->
            @if (tab() === "extras") {
              <div
                class="rounded-2xl border border-zinc-800 bg-zinc-900 p-5 space-y-5"
              >
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Shoes
                  </p>
                  <div class="grid grid-cols-3 gap-2">
                    @for (sh of shoeStyles; track sh) {
                      <button
                        (click)="s('shoeStyle', sh)"
                        [class.sel]="cfg().shoeStyle === sh"
                        class="opt border border-zinc-700 text-zinc-400 py-3 rounded-xl text-xs font-bold"
                      >
                        {{ sh }}
                      </button>
                    }
                  </div>
                </div>
                @if (cfg().shoeStyle !== "None") {
                  <div>
                    <p
                      class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                    >
                      Shoe Color
                    </p>
                    <div class="grid grid-cols-3 gap-3">
                      @for (e of shoeColorE; track e[0]) {
                        <button
                          (click)="s('shoeColor', e[0])"
                          [class.sel]="cfg().shoeColor === e[0]"
                          class="swatch opt border border-zinc-700 rounded-xl py-3 flex flex-col items-center gap-1.5 text-xs text-zinc-400 font-bold"
                        >
                          <span
                            class="w-6 h-6 rounded-full block"
                            [style.background-color]="e[1].fill"
                          ></span>
                          {{ e[1].label }}
                        </button>
                      }
                    </div>
                  </div>
                }
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Outerwear
                  </p>
                  <div class="grid grid-cols-2 gap-2">
                    @for (o of outerwears; track o) {
                      <button
                        (click)="s('outerwear', o)"
                        [class.sel]="cfg().outerwear === o"
                        class="opt border border-zinc-700 text-zinc-400 py-3 rounded-xl text-xs font-bold"
                      >
                        {{ o }}
                      </button>
                    }
                  </div>
                </div>
                <div>
                  <p
                    class="text-[10px] text-zinc-500 uppercase tracking-widest font-bold mb-3"
                  >
                    Accessories
                  </p>
                  <div class="grid grid-cols-3 gap-2">
                    @for (a of accs; track a) {
                      <button
                        (click)="s('accessory', a)"
                        [class.sel]="cfg().accessory === a"
                        class="opt border border-zinc-700 text-zinc-400 py-3 rounded-xl text-xs font-bold"
                      >
                        {{ a }}
                      </button>
                    }
                  </div>
                </div>
              </div>
            }
          </div>
        </div>
      </div>
    </div>
  `,
})
export class MimojiComponent implements OnInit {
  SKIN = SKIN;
  EYE_COL = EYE_COL;

  fashionService = inject(FashionService);

  cfg = signal<AvatarCfg>({
    skin: "Medium",
    hairStyle: "Fade",
    hairColor: "Midnight",
    eyeColor: "Espresso",
    faceShape: "Oval",
    nose: "Button",
    mouth: "Smile",
    accessory: "None",
    topStyle: "Tee",
    topColor: "Black",
    bottomStyle: "Jeans",
    bottomColor: "Dark Denim",
    shoeStyle: "Sneakers",
    shoeColor: "White Sneakers",
    outerwear: "None",
    equippedTopId: undefined,
    equippedBottomId: undefined,
    equippedShoeId: undefined,
    equippedOuterwearId: undefined,
  });

  wardrobe = signal<WardrobeItem[]>([]);
  tab = signal("wardrobe");
  saving = signal(false);
  msg = signal<"ok" | "err" | null>(null);

  tabs = [
    { id: "wardrobe", label: "My Wardrobe", icon: "🧥" },
    { id: "skin", label: "Skin", icon: "🏼" },
    { id: "hair", label: "Hair", icon: "💇" },
    { id: "face", label: "Face", icon: "👁" },
    { id: "top", label: "Top", icon: "👕" },
    { id: "bottom", label: "Bottom", icon: "👖" },
    { id: "extras", label: "Extras", icon: "👟" },
  ];

  // Grouped wardrobe items
  wardrobeTops = computed(() =>
    this.wardrobe().filter((i) =>
      ["SHIRT", "OUTERWEAR"].includes(i.product.category),
    ),
  );
  wardrobeBottoms = computed(() =>
    this.wardrobe().filter((i) => i.product.category === "PANTS"),
  );
  wardrobeShoes = computed(() =>
    this.wardrobe().filter((i) => i.product.category === "SHOES"),
  );
  wardrobeAccs = computed(() =>
    this.wardrobe().filter((i) => i.product.category === "ACCESSORY"),
  );

  // Currently wearing display names
  wearingTop = computed(
    () =>
      this.wardrobe().find((i) => i.product.id === this.cfg().equippedTopId)
        ?.product.name,
  );
  wearingBottom = computed(
    () =>
      this.wardrobe().find((i) => i.product.id === this.cfg().equippedBottomId)
        ?.product.name,
  );
  wearingShoe = computed(
    () =>
      this.wardrobe().find((i) => i.product.id === this.cfg().equippedShoeId)
        ?.product.name,
  );

  hairStyles: HairStyle[] = [
    "Fade",
    "Buzz",
    "Curly",
    "Wavy",
    "Long",
    "Bob",
    "Bun",
    "Bald",
    "Locs",
    "Mohawk",
  ];
  faceShapes: FaceShape[] = ["Oval", "Round", "Square", "Heart"];
  noses: Nose[] = ["Button", "Straight", "Wide"];
  mouths: Mouth[] = ["Smile", "Neutral", "Smirk", "Grin"];
  accs: AccessoryT[] = ["None", "Glasses", "Sunglasses", "Earrings", "Chain"];
  topStyles: TopStyle[] = [
    "Tee",
    "Shirt",
    "Hoodie",
    "Turtleneck",
    "Tank",
    "Blazer",
    "None",
  ];
  bottomStyles: BottomStyle[] = [
    "Jeans",
    "Chinos",
    "Shorts",
    "Joggers",
    "Skirt",
    "None",
  ];
  shoeStyles: ShoeStyle[] = [
    "Sneakers",
    "Dress Shoes",
    "Boots",
    "High Tops",
    "None",
  ];
  outerwears: OuterwearStyle[] = [
    "None",
    "Bomber",
    "Denim Jacket",
    "Trenchcoat",
  ];

  skinE = Object.entries(SKIN);
  hairE = Object.entries(HAIR_COL);
  eyeE = Object.entries(EYE_COL);
  topColorE = Object.entries(TOP_COLORS);
  bottomColorE = Object.entries(BOTTOM_COLORS);
  shoeColorE = Object.entries(SHOE_COLORS);

  curly = [
    { cx: 52, cy: 38, r: 14 },
    { cx: 66, cy: 28, r: 12 },
    { cx: 80, cy: 24, r: 11 },
    { cx: 94, cy: 28, r: 12 },
    { cx: 108, cy: 38, r: 14 },
    { cx: 60, cy: 46, r: 10 },
    { cx: 100, cy: 46, r: 10 },
  ];

  ngOnInit() {
    this.fashionService.getProfile().subscribe({
      next: (p) => {
        if (p?.avatarConfig)
          this.cfg.set({
            ...this.cfg(),
            ...(p.avatarConfig as unknown as AvatarCfg),
          });
      },
    });

    this.fashionService.getWardrobe().subscribe({
      next: (items) => this.wardrobe.set(items),
    });
  }

  equip(item: WardrobeItem) {
    const p = item.product;
    if (p.category === "SHIRT") {
      this.cfg.update((c) => ({
        ...c,
        equippedTopId: p.id,
        topStyle: this.mapTop(p.name),
        topColor: this.mapColor(p.name, "top"),
      }));
    } else if (p.category === "PANTS") {
      this.cfg.update((c) => ({
        ...c,
        equippedBottomId: p.id,
        bottomStyle: this.mapBottom(p.name),
        bottomColor: this.mapColor(p.name, "bottom"),
      }));
    } else if (p.category === "SHOES") {
      this.cfg.update((c) => ({
        ...c,
        equippedShoeId: p.id,
        shoeStyle: this.mapShoe(p.name),
        shoeColor: this.mapColor(p.name, "shoe"),
      }));
    } else if (p.category === "OUTERWEAR") {
      this.cfg.update((c) => ({
        ...c,
        equippedOuterwearId: p.id,
        outerwear: this.mapOuter(p.name),
      }));
    }
  }

  private mapTop(name: string): TopStyle {
    const n = name.toLowerCase();
    if (n.includes("hoodie")) return "Hoodie";
    if (n.includes("shirt")) return "Shirt";
    if (n.includes("tank")) return "Tank";
    if (n.includes("blazer")) return "Blazer";
    if (n.includes("turtleneck")) return "Turtleneck";
    return "Tee";
  }

  private mapBottom(name: string): BottomStyle {
    const n = name.toLowerCase();
    if (n.includes("chino")) return "Chinos";
    if (n.includes("short")) return "Shorts";
    if (n.includes("jogger")) return "Joggers";
    if (n.includes("skirt")) return "Skirt";
    return "Jeans";
  }

  private mapShoe(name: string): ShoeStyle {
    const n = name.toLowerCase();
    if (n.includes("boot")) return "Boots";
    if (n.includes("dress") || n.includes("oxford")) return "Dress Shoes";
    if (n.includes("high")) return "High Tops";
    return "Sneakers";
  }

  private mapOuter(name: string): OuterwearStyle {
    const n = name.toLowerCase();
    if (n.includes("bomber")) return "Bomber";
    if (n.includes("denim")) return "Denim Jacket";
    if (n.includes("trench")) return "Trenchcoat";
    return "None";
  }

  private mapColor(name: string, type: "top" | "bottom" | "shoe"): string {
    const n = name.toLowerCase();
    if (type === "top") {
      if (n.includes("white")) return "White";
      if (n.includes("navy")) return "Navy";
      if (n.includes("grey")) return "Grey";
      if (n.includes("gold")) return "Gold";
      if (n.includes("crimson") || n.includes("red")) return "Crimson";
      if (n.includes("olive")) return "Olive";
      if (n.includes("blue")) return "Sky";
      return "Black";
    } else if (type === "bottom") {
      if (n.includes("denim")) return "Dark Denim";
      if (n.includes("khaki")) return "Khaki";
      if (n.includes("grey")) return "Grey";
      if (n.includes("white")) return "White";
      if (n.includes("olive")) return "Olive";
      if (n.includes("charcoal")) return "Charcoal";
      return "Black";
    } else {
      if (n.includes("leather") || n.includes("black")) return "Black Leather";
      if (n.includes("brown") || n.includes("oxford")) return "Brown Oxfords";
      if (n.includes("gold")) return "Gold Boots";
      if (n.includes("grey") || n.includes("runner")) return "Grey Runners";
      if (n.includes("red")) return "Red High-Top";
      return "White Sneakers";
    }
  }

  s<K extends keyof AvatarCfg>(key: K, val: AvatarCfg[K]) {
    this.cfg.update((c) => ({ ...c, [key]: val }));
  }

  hairFill = computed(() => HAIR_COL[this.cfg().hairColor] ?? "#0D0D0D");
  topFill = computed(() => TOP_COLORS[this.cfg().topColor]?.fill ?? "#1a1a1a");
  topShadow = computed(() => TOP_COLORS[this.cfg().topColor]?.shadow ?? "#000");
  bottomFill = computed(
    () => BOTTOM_COLORS[this.cfg().bottomColor]?.fill ?? "#1C2D4A",
  );
  bottomShadow = computed(
    () => BOTTOM_COLORS[this.cfg().bottomColor]?.shadow ?? "#0c1828",
  );
  shoeFill = computed(
    () => SHOE_COLORS[this.cfg().shoeColor]?.fill ?? "#f0ece4",
  );
  shoeSole = computed(
    () => SHOE_COLORS[this.cfg().shoeColor]?.sole ?? "#d0ccc4",
  );

  faceRx = computed(
    () =>
      ({ Oval: 32, Round: 36, Square: 34, Heart: 32 })[this.cfg().faceShape] ??
      32,
  );
  faceRy = computed(
    () =>
      ({ Oval: 40, Round: 36, Square: 37, Heart: 39 })[this.cfg().faceShape] ??
      40,
  );

  lt(hex: string, a: number): string {
    if (!hex?.startsWith("#")) return hex;
    const n = parseInt(hex.slice(1), 16);
    const r = Math.min(255, (n >> 16) + a),
      g = Math.min(255, ((n >> 8) & 0xff) + a),
      b = Math.min(255, (n & 0xff) + a);
    return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)}`;
  }
  dk(name: string, a: number): string {
    const hex = HAIR_COL[name] ?? SKIN[name] ?? name;
    if (!hex?.startsWith("#")) return hex;
    const n = parseInt(hex.slice(1), 16);
    const r = Math.max(0, (n >> 16) - a),
      g = Math.max(0, ((n >> 8) & 0xff) - a),
      b = Math.max(0, (n & 0xff) - a);
    return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)}`;
  }

  save() {
    this.saving.set(true);
    this.fashionService.updateProfile({ avatarConfig: this.cfg() }).subscribe({
      next: () => {
        this.saving.set(false);
        this.msg.set("ok");
        setTimeout(() => this.msg.set(null), 3000);
      },
      error: () => {
        this.saving.set(false);
        this.msg.set("err");
        setTimeout(() => this.msg.set(null), 3000);
      },
    });
  }
}
