import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth.component';
import { CatalogComponent } from './components/catalog/catalog.component';
import { CartComponent } from './components/cart/cart.component';
import { OrdersComponent } from './components/orders/orders.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  // Rotte Pubbliche (visibili a tutti)
  { path: '', component: CatalogComponent },
  { path: 'catalog', component: CatalogComponent },
  { path: 'auth', component: AuthComponent },
  
  // Rotte Protette dal Guardiano (accessibili solo con Token JWT valido)
  { path: 'cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [authGuard] },
  
  // Fallback per rotte sconosciute: reindirizza alla Home
  { path: '**', redirectTo: '' }
];
