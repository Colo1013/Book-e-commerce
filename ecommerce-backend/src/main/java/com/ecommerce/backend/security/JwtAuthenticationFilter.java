package com.ecommerce.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Questo è il famigerato "Filtro". Si piazza esattamente a metà strada 
 * tra la richiesta HTTP in arrivo e il nostro Controller.
 * Estende OncePerRequestFilter perché deve essere eseguito UNA SOLA VOLTA ad ogni richiesta HTTP.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Andiamo a cercare l'header "Authorization" nella richiesta HTTP
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // Se non c'è l'header, o se non inizia con "Bearer ", ignoriamo e passiamo oltre (magari era una richiesta pubblica come il login)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. Estraiamo il token puro (saltando i primi 7 caratteri che sono "Bearer ")
        jwt = authHeader.substring(7);
        
        // 3. Chiediamo al JwtService di estrarre l'email. 
        // ATTENZIONE: Questo passaggio esegue anche la validazione matematica della firma!
        userEmail = jwtService.extractUsername(jwt);
        
        // Se c'è un'email nel token e Spring non ha ancora registrato questo utente come "Loggato" per questa richiesta
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Prendiamo i dati dell'utente dal DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Verifichiamo che il token non sia scaduto e sia coerente
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // 4. Creiamo il "Pass" ufficiale di Spring Security per dirgli che l'utente è autenticato
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 5. SCRIVIAMO NELLA MEMORIA A BREVE TERMINE DI SPRING (SecurityContextHolder).
                // Spring si ricorderà di questo utente SOLO per la durata di questa specifica richiesta HTTP!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Passiamo la palla al prossimo filtro o al Controller
        filterChain.doFilter(request, response);
    }
}
