import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {
  isLoggedIn$: Observable<boolean>;
  cartItemCount$: Observable<number>;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.cartItemCount$ = this.cartService.itemCount$;
  }

  ngOnInit(): void {
    // Ci iscriviamo al flusso reattivo dello stato di login:
    // ogni volta che l'utente effettua l'accesso (o se è già loggato all'avvio),
    // sincronizziamo automaticamente il conteggio degli articoli nel badge del carrello.
    this.isLoggedIn$.subscribe(loggedIn => {
      if (loggedIn) {
        this.cartService.getCart().subscribe({
          error: () => {} // L'eventuale token scaduto viene gestito automaticamente dall'AuthInterceptor
        });
      } else {
        this.cartService.clearLocalCount();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.cartService.clearLocalCount();
    this.router.navigate(['/auth']);
  }
}
