import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { CartResponse } from '../../models/cart';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  // Oggetto contenente le righe carrello e il totale calcolato dal backend
  cart: CartResponse | null = null;
  isLoading = true;
  isCheckingOut = false;
  errorMessage = '';
  successMessage = '';

  // Gestione del modulo di Pagamento Simulato
  showPaymentForm = false;
  shippingAddress = '';
  cardHolderName = '';
  cardNumber = '';
  cardExpiry = '';
  cardCvv = '';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  // Interroga GET /api/v1/cart tramite CartService
  loadCart(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cart = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Impossibile recuperare il carrello. Riprova più tardi.';
        this.isLoading = false;
      }
    });
  }

  // Interroga DELETE /api/v1/cart/items/{isbn}
  removeItem(isbn: string): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.cartService.removeFromCart(isbn).subscribe({
      next: (updatedCart) => {
        this.cart = updatedCart;
        this.successMessage = 'Libro rimosso dal carrello con successo.';
      },
      error: (err) => {
        this.errorMessage = 'Errore durante la rimozione del libro.';
      }
    });
  }

  // Mostra il modulo di inserimento dati per il pagamento simulato
  openPaymentStep(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.showPaymentForm = true;
  }

  // Annulla lo step di pagamento e torna al riepilogo semplice
  cancelPaymentStep(): void {
    this.showPaymentForm = false;
    this.errorMessage = '';
  }

  // Valida i campi del pagamento simulato e interroga POST /api/v1/orders/checkout
  proceedToCheckout(): void {
    this.errorMessage = '';
    this.successMessage = '';

    // Validazione formale dei dati di spedizione e carta simulata
    if (!this.shippingAddress.trim() || !this.cardHolderName.trim() || !this.cardNumber.trim() || !this.cardExpiry.trim() || !this.cardCvv.trim()) {
      this.errorMessage = 'Compila tutti i campi di spedizione e pagamento per confermare l\'ordine.';
      return;
    }

    if (this.cardNumber.replace(/\s+/g, '').length < 13) {
      this.errorMessage = 'Inserisci un numero di carta valido (almeno 13-16 cifre).';
      return;
    }

    if (this.cardCvv.trim().length < 3) {
      this.errorMessage = 'Il codice di sicurezza CVV deve contenere almeno 3 cifre.';
      return;
    }

    this.isCheckingOut = true;

    this.orderService.checkout().subscribe({
      next: (createdOrder) => {
        // Resettiamo il contatore articoli nella Navbar
        this.cartService.clearLocalCount();
        this.isCheckingOut = false;
        this.showPaymentForm = false;
        // Reindirizziamo l'utente alla schermata degli ordini per vedere la ricevuta
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        this.isCheckingOut = false;
        // Se Spring Boot lancia 409 Conflict (Optimistic Lock) o 400 (scorte insufficienti)
        this.errorMessage = err.error?.message || 'Errore durante il checkout: scorte esaurite o conflitto di concorrenza.';
      }
    });
  }
}
