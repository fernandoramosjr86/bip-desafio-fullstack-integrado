import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Beneficio,
  BeneficioRequest,
  PageResponse,
  TransferenciaRequest
} from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficioApiService {
  private readonly baseUrl = '/api/v1/beneficios';

  constructor(private readonly http: HttpClient) {}

  listar(page: number, size: number): Observable<PageResponse<Beneficio>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Beneficio>>(this.baseUrl, { params });
  }

  criar(payload: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, payload);
  }

  atualizar(id: number, payload: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, payload);
  }

  transferir(payload: TransferenciaRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/transferencias`, payload);
  }
}
