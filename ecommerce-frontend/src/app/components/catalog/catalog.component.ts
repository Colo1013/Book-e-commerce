import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Book } from '../../models/book';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog.component.html'
})
export class CatalogComponent implements OnInit {
  // Lista dei libri ricevuti dal backend
  books: Book[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';

  // Testo digitato dall'utente nella barra di ricerca
  searchTerm = '';

  // Dizionario reattivo delle quantità selezionate dall'utente (chiave: isbn, valore: quantità)
  quantities: { [isbn: string]: number } = {};

  constructor(
    private bookService: BookService,
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  // Lifecycle hook: invocato automaticamente da Angular subito dopo la creazione del componente
  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(search?: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Sottoscrizione asincrona alla chiamata HTTP esposta da BookService
    this.bookService.getBooks(search).subscribe({
      next: (data) => {
        this.books = data;
        // Inizializziamo il selettore di quantità a 1 per ogni libro caricato
        this.books.forEach(book => {
          this.quantities[book.isbn] = 1;
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Impossibile comunicare con il server. Assicurati che Spring Boot sia avviato sulla porta 8080.';
        this.isLoading = false;
      }
    });
  }

  // Avvia la ricerca filtrando per il testo inserito dall'utente
  onSearch(): void {
    this.loadBooks(this.searchTerm);
  }

  // Pulisce il filtro di ricerca e ricarica l'intero catalogo
  resetSearch(): void {
    this.searchTerm = '';
    this.loadBooks();
  }

  addToCart(book: Book): void {
    this.errorMessage = '';
    this.successMessage = '';

    // Validazione difensiva: se l'utente non è autenticato, lo indirizziamo al login
    if (!this.authService.hasToken()) {
      this.router.navigate(['/auth']);
      return;
    }

    const qty = this.quantities[book.isbn] || 1;

    // Controllo preliminare sulla disponibilità di magazzino
    if (qty > book.stockQuantity) {
      this.errorMessage = `La quantità richiesta (${qty}) supera le copie attualmente in stock (${book.stockQuantity}).`;
      return;
    }

    // Chiamata reale a Spring Boot (POST /api/v1/cart/items)
    this.cartService.addToCart({ isbn: book.isbn, quantity: qty }).subscribe({
      next: (cart) => {
        this.successMessage = `Aggiunte con successo ${qty} copie di "${book.title}" al carrello!`;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Errore durante l\'aggiunta del libro al carrello.';
      }
    });
  }
}
