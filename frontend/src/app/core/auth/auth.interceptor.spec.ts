import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { authInterceptor } from './auth.interceptor';
import { SessionStore } from './session-store.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let session: SessionStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    session = TestBed.inject(SessionStore);
    session.clear();
  });

  afterEach(() => httpTesting.verify());

  it('attaches the bearer token to private API requests', () => {
    session.save('signed-token', 'ADMINISTRADOR');
    http.get('/api/admin/pacientes').subscribe();

    const request = httpTesting.expectOne('/api/admin/pacientes');
    expect(request.request.headers.get('Authorization')).toBe('Bearer signed-token');
    request.flush([]);
  });

  it('does not attach a bearer token to public requests', () => {
    session.save('signed-token', 'PACIENTE');
    http.get('/api/pacientes/publico').subscribe();

    const request = httpTesting.expectOne('/api/pacientes/publico');
    expect(request.request.headers.has('Authorization')).toBe(false);
    request.flush([]);
  });
});
