import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiCallService {

  constructor(private http: HttpClient) { }

  searchPatients(): Observable<any> {
    return this.http.get("http://localhost:8080/Patient/");
  }
  //Test
  searchOrgs(): Observable<any> {
    return this.http.get("http://localhost:8080/Organization/");
  }
}
