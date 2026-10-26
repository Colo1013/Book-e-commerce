import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  standalone: true,
  // Importiamo CommonModule per *ngIf e FormsModule per leggere i campi del form
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html'
})
export class AuthComponent {
  
  // Il "Cervello" del componente: Decide se mostrare la grafica di Login o Registrazione
  isLoginMode = true; 
  
  // Le variabili collegate ai campi di testo
  email = '';
  password = '';
  name = '';
  surname = '';

  errorMessage = '';

  // Iniettiamo i servizi necessari
  constructor(private authService: AuthService, private router: Router) {}

  // Tasto per switchare grafica (da Login a Registrazione e viceversa)
  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
  }

  // Scatenato dal tasto "Invia" del form HTML
  onSubmit() {
    this.errorMessage = '';

    if (this.isLoginMode) {
      
      // FLUSSO DI LOGIN
      this.authService.login({ email: this.email, password: this.password }).subscribe({
        next: () => {
          // Se la rete risponde con successo, il token è stato salvato. Teletrasporto alla Home!
          this.router.navigate(['/']);
        },
        error: (err) => {
          // Se la rete risponde con 401 Unauthorized o 404 (da Spring Boot)
          this.errorMessage = 'Credenziali errate o utente inesistente.';
        }
      });
      
    } else {
      
      // FLUSSO DI REGISTRAZIONE
      const userData = { email: this.email, password: this.password, firstName: this.name, lastName: this.surname };
      
      this.authService.register(userData).subscribe({
        next: () => {
          // Se la registrazione ha successo, svuotiamo i campi e passiamo al Login
          this.isLoginMode = true;
          this.errorMessage = '';
          alert('Registrazione completata! Ora puoi effettuare l\'accesso.');
        },
        error: (err) => {
          // Se Spring Boot risponde con 409 Conflict (DuplicateResourceException)
          this.errorMessage = 'Errore durante la registrazione. Questa email è già in uso?';
        }
      });
      
    }
  }
}
