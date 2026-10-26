import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../models/book';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  // Endpoint pubblico di Spring Boot per i libri
  private apiUrl = 'http://localhost:8080/api/v1/books';

  constructor(private http: HttpClient) {}

  // Recupera i libri presenti a catalogo (con filtro opzionale di ricerca per titolo o autore)
  getBooks(search?: string): Observable<Book[]> {
    let params = new HttpParams();
    if (search && search.trim().length > 0) {
      params = params.set('search', search.trim());
    }
    return this.http.get<Book[]>(this.apiUrl, { params });
  }

  // Recupera un singolo libro tramite la sua chiave di business (ISBN)
  getBookByIsbn(isbn: string): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/${isbn}`);
  }
}
