import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VoteService {

  private apiUrl = 'http://localhost:8080/votes/vote';

  constructor(private http: HttpClient) { }

  vote(movieId: number, voteType: 'LIKE' | 'HATE'): Observable<any> {
    let params = new HttpParams().set('movieId', movieId.toString()).set('voteType', voteType);
    return this.http.post(this.apiUrl, null, { params });
  }

  removeVote(movieId: number): Observable<any> {
    let params = new HttpParams().set('movieId', movieId.toString());
    return this.http.delete(this.apiUrl, { params });
  }
}