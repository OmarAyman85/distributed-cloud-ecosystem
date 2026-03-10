import { bootstrapApplication } from '@angular/platform-browser';
import { Component } from '@angular/core';
import { RouterOutlet, provideRouter } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <h1>Welcome to Vox Frontend!</h1>
    <router-outlet></router-outlet>
  `,
})
export class AppComponent {}

bootstrapApplication(AppComponent, {
  providers: [provideRouter([])]
}).catch((err) => console.error(err));
