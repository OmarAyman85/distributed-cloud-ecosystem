import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdvisorService } from '../../services/advisor.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-advisor',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './advisor.component.html',
  styleUrls: ['./advisor.component.scss'],
})
export class AdvisorComponent implements OnInit {
  private advisorService = inject(AdvisorService);

  advice = signal<string>('');
  isLoading = signal<boolean>(false);
  error = signal<string>('');

  ngOnInit() {
    this.fetchAdvice();
  }

  fetchAdvice() {
    this.isLoading.set(true);
    this.error.set('');
    this.advice.set('');

    this.advisorService.getSuggestions().subscribe({
      next: (response) => {
        this.advice.set(response.advice);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to get AI advice', err);
        this.error.set(
          'We encountered an issue connecting to your Financial AI Advisor. Please try again later.',
        );
        this.isLoading.set(false);
      },
    });
  }

  // Simple Markdown to HTML parser for structure
  formatAdvice(text: string): string {
    if (!text) return '';

    // Replace markdown headers with HTML headers
    let formatted = text.replace(
      /^### (.*$)/gim,
      '<h3 class="text-xl font-bold mt-6 mb-3 text-txt-main">$1</h3>',
    );
    formatted = formatted.replace(
      /^## (.*$)/gim,
      '<h2 class="text-2xl font-bold mt-8 mb-4 border-b border-border-glass pb-2 text-txt-main">$1</h2>',
    );
    formatted = formatted.replace(
      /^# (.*$)/gim,
      '<h1 class="text-3xl font-bold mt-8 mb-4 text-txt-main">$1</h1>',
    );

    // Replace bold text
    formatted = formatted.replace(
      /\*\*(.*?)\*\*/gim,
      '<strong class="font-bold text-txt-main">$1</strong>',
    );

    // Replace bullets
    formatted = formatted.replace(
      /^\* (.*$)/gim,
      '<li class="ml-4 mb-2">$1</li>',
    );
    formatted = formatted.replace(
      /^- (.*$)/gim,
      '<li class="ml-4 mb-2">$1</li>',
    );

    // Fix lists by wrapping them (very simple heuristic)
    formatted = formatted.replace(
      /(<li.*<\/li>\n*)+/gim,
      '<ul class="list-disc mb-4">$&</ul>',
    );

    // Replace newlines with paragraphs
    formatted = formatted.replace(/\n\n/gim, '</p><p class="mb-4">');

    return `<div class="prose prose-invert max-w-none text-txt-secondary leading-relaxed"><p>${formatted}</p></div>`;
  }
}
