import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AddToCartRequest, CartResponse } from '../models/cart';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // Base URL per le operazioni sul carrello in Spring Boot
  private apiUrl = 'http://localhost:8080/api/v1/cart';

  // Flusso reattivo (BehaviorSubject) per tracciare il numero totale di articoli nel carrello
  private itemCountSubject = new BehaviorSubject<number>(0);
  public itemCount$ = this.itemCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Chiamata GET /api/v1/cart: recupera il carrello associato all'email del token JWT
  getCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.apiUrl).pipe(
      tap(cart => this.updateCount(cart))
    );
  }

  // Chiamata POST /api/v1/cart/items: invia isbn e quantità richiesta
  addToCart(request: AddToCartRequest): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/items`, request).pipe(
      tap(cart => this.updateCount(cart))
    );
  }

  // Chiamata DELETE /api/v1/cart/items/{isbn}: elimina la riga del libro dal carrello
  removeFromCart(isbn: string): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${this.apiUrl}/items/${isbn}`).pipe(
      tap(cart => this.updateCount(cart))
    );
  }

  // Metodo privato per calcolare la somma totale delle copie e aggiornare il megafono reattivo
  private updateCount(cart: CartResponse): void {
    const total = cart.items ? cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
    this.itemCountSubject.next(total);
  }

  // Resetta il conteggio in memoria (utile durante il logout o a completamento del checkout)
  clearLocalCount(): void {
    this.itemCountSubject.next(0);
  }
}
