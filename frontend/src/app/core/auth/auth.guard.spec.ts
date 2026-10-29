import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { authGuard } from './auth.guard';
import { roleGuard } from './role.guard';
import { SessionStore } from './session-store.service';

describe('route guards', () => {
  let session: SessionStore;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])],
    });
    session = TestBed.inject(SessionStore);
    router = TestBed.inject(Router);
    session.clear();
  });

  it('redirects anonymous visitors away from private routes', () => {
    const result = runGuard(authGuard, {}, '/admin/pacientes');

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toContain('/login');
  });

  it('allows an authenticated administrator through the role guard', () => {
    session.save('signed-token', 'ADMINISTRADOR');

    expect(runGuard(roleGuard, { role: 'ADMINISTRADOR' })).toBe(true);
  });

  it('redirects a patient away from administrator routes', () => {
    session.save('signed-token', 'PACIENTE');
    const result = runGuard(roleGuard, { role: 'ADMINISTRADOR' });

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/pacientes');
  });

  function runGuard(
    guard: typeof authGuard | typeof roleGuard,
    data: Record<string, string>,
    url = '/admin/pacientes',
  ) {
    const route = { data } as ActivatedRouteSnapshot;
    const state = { url } as RouterStateSnapshot;
    return TestBed.runInInjectionContext(() => guard(route, state));
  }
});
