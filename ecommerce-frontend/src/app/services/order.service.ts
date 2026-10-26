import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderResponse } from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  // Base URL per le operazioni sugli ordini in Spring Boot
  private apiUrl = 'http://localhost:8080/api/v1/orders';

  constructor(private http: HttpClient) {}

  // Chiamata POST /api/v1/orders/checkout: trasforma il carrello dell'utente in una ricevuta definitiva
  checkout(): Observable<OrderResponse> {
    // Non inviamo nessun body JSON: l'identità è garantita dal token JWT iniettato dall'AuthInterceptor
    return this.http.post<OrderResponse>(`${this.apiUrl}/checkout`, {});
  }

  // Chiamata GET /api/v1/orders: recupera lo storico degli acquisti passati dell'utente ordinati per data discendente
  getMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(this.apiUrl);
  }
}
