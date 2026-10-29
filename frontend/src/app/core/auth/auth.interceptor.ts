import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionStore } from './session-store.service';

const PUBLIC_ENDPOINTS = ['/api/auth/login', '/api/auth/register', '/api/pacientes/publico'];

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const path = new URL(request.url, 'http://localhost').pathname;
  const isApiRequest = path.startsWith('/api/');
  const isPublicRequest = PUBLIC_ENDPOINTS.includes(path);
  const token = inject(SessionStore).token();

  if (!isApiRequest || isPublicRequest || !token) {
    return next(request);
  }

  return next(request.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  }));
};
