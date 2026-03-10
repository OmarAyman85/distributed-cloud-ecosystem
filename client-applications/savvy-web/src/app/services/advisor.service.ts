import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdvisorResponse {
  advice: string;
}

@Injectable({
  providedIn: 'root',
})
export class AdvisorService {
  private http = inject(HttpClient);
  private apiUrl = '/api/advisor';

  getSuggestions(): Observable<AdvisorResponse> {
    return this.http.get<AdvisorResponse>(`${this.apiUrl}/suggestions`);
  }
}
