import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Saving } from '../models/dashboard';

@Injectable({
  providedIn: 'root',
})
export class SavingService {
  private http = inject(HttpClient);
  private apiUrl = '/api/saving';

  createSaving(savingData: Partial<Saving>): Observable<Saving> {
    return this.http.post<Saving>(this.apiUrl, savingData);
  }

  getAllSavings(): Observable<Saving[]> {
    return this.http.get<Saving[]>(`${this.apiUrl}/all`);
  }

  getSavingById(id: number): Observable<Saving> {
    return this.http.get<Saving>(`${this.apiUrl}/${id}`);
  }

  updateSaving(id: number, savingData: Partial<Saving>): Observable<Saving> {
    return this.http.put<Saving>(`${this.apiUrl}/${id}`, savingData);
  }

  deleteSaving(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
