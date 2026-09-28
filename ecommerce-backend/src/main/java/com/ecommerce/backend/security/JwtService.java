package com.ecommerce.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * La "Fabbrica e Verificatore" dei Token.
 * Contiene tutta la matematica crittografica per generare e convalidare i JWT.
 */
@Service
public class JwtService {

    // Questa è la "Secret Key" (Timbro segreto) di cui parlavamo!
    // Nelle applicazioni reali, non va MAI scritta nel codice sorgente, ma messa in file di configurazione segreti.
    // Questa stringa è codificata in Base64 e contiene almeno 256 bit per soddisfare l'algoritmo HS256.
    private static final String SECRET_KEY = "VGhpcy1pcy1hLXZlcnktc2VjdXJlLWtleS1mb3Itand0LWluLXNwcmluZy1ib290LWFwcGxpY2F0aW9u";

    // Estrae l'email (il "Subject") dal token decodificando il Payload
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Metodo generico per estrarre qualsiasi pezzo di informazione (Claim) dal Payload
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Genera un token base
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Genera il token mettendo insieme i 3 pezzi (Header, Payload, Signature)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Inserisce l'email
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data di creazione
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Scade dopo 24 ore
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma matematicamente!
                .compact();
    }

    // Verifica se il token appartiene all'utente e se non è scaduto
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Qui avviene la magia crittografica inversa. 
    // Se un hacker manomette il token, questo metodo fallirà tirando un'eccezione!
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Decodifica la stringa della chiave in un oggetto crittografico di tipo Key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
