import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';

// In Angular 17+ gli interceptor sono funzioni pure, non classi.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Funzione magica inject() per recuperare le dipendenze in una funzione
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. IL POSTINO: Prende il token dal LocalStorage
  const token = authService.getToken();
  
  let modifiedReq = req;
  
  if (token) {
    // REGOLA ANGULAR: Le richieste HTTP in volo sono "Immutabili" (sola lettura).
    // Dobbiamo clonare la richiesta originale per potergli appiccicare il francobollo (L'Header).
    modifiedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // Stessa sintassi che usavamo su Postman
      }
    });
  }

  // 2. IL CONTROLLORE: Invia la richiesta modificata verso Spring Boot e aspetta la risposta
  return next(modifiedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Se Spring Boot ci sbatte la porta in faccia (Token scaduto dopo 24h, o manomesso)
      if (error.status === 401 || error.status === 403) {
        
        // Eseguiamo il logout forzato (cancellando il token falso/scaduto dal disco)
        authService.logout();
        
        // Lo teletrasportiamo alla pagina di Login
        router.navigate(['/auth']); 
      }
      // Rilanciamo l'errore per farlo gestire a eventuali altri componenti
      return throwError(() => error); 
    })
  );
};
