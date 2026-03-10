import { Injectable, signal, effect } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  darkMode = signal<boolean>(this.loadPreference());

  constructor() {
    effect(() => {
      if (this.darkMode()) {
        document.body.classList.add('dark-theme');
        localStorage.setItem('theme', 'dark');
      } else {
        document.body.classList.remove('dark-theme');
        localStorage.setItem('theme', 'light');
      }
    });
  }

  toggle() {
    this.darkMode.update((v) => !v);
  }

  private loadPreference(): boolean {
    const saved = localStorage.getItem('theme');
    if (saved) return saved === 'dark';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }
}
