import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

// In Angular 17, i Guard sono funzioni pure
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Il Buttafuori: controlliamo se l'utente ha il passaporto (Token)
  if (authService.hasToken()) {
    return true; // La porta si apre, l'utente può passare
  } else {
    // Accesso negato: rispediamo l'utente alla pagina di login
    router.navigate(['/auth']);
    return false;
  }
};
