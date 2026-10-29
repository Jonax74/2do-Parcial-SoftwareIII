import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { UserRegistrationFormComponent } from './user-registration-form.component';

describe('UserRegistrationFormComponent', () => {
  let fixture: ComponentFixture<UserRegistrationFormComponent>;
  let auth: {
    registerPatient: ReturnType<typeof vi.fn>;
    registerUser: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    auth = {
      registerPatient: vi.fn().mockReturnValue(
        of({ id: 1, nombre: 'Ana Pérez', correo: 'ana@example.com', rol: 'PACIENTE' }),
      ),
      registerUser: vi.fn().mockReturnValue(
        of({ id: 2, nombre: 'Admin', correo: 'admin@example.com', rol: 'ADMINISTRADOR' }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [UserRegistrationFormComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: auth },
      ],
    }).compileComponents();
  });

  it('only offers patient self-registration to public visitors', () => {
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#rol')).toBeNull();
    expect(fixture.nativeElement.querySelector('#documento')).toBeTruthy();
  });

  it('submits the public patient form and confirms successful registration', () => {
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.detectChanges();

    setInput('#nombre', 'Ana Pérez');
    setInput('#registro-correo', 'ana@example.com');
    setInput('#registro-password', 'password123');
    setInput('#documento', '123456');
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(auth.registerPatient).toHaveBeenCalledWith(
      expect.objectContaining({
        nombre: 'Ana Pérez',
        correo: 'ana@example.com',
        documento: '123456',
      }),
    );
    expect(fixture.nativeElement.textContent).toContain('se crearon correctamente');
  });

  it('lets an administrator create an administrator account', () => {
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.componentRef.setInput('adminMode', true);
    fixture.detectChanges();

    setInput('#nombre', 'Admin');
    setInput('#registro-correo', 'admin@example.com');
    setInput('#registro-password', 'password123');
    const role = fixture.nativeElement.querySelector('#rol') as HTMLSelectElement;
    role.value = 'ADMINISTRADOR';
    role.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(auth.registerUser).toHaveBeenCalledWith(
      expect.objectContaining({
        nombre: 'Admin',
        correo: 'admin@example.com',
        rol: 'ADMINISTRADOR',
      }),
    );
    expect(fixture.nativeElement.textContent).toContain('registró correctamente');
  });

  function setInput(selector: string, value: string): void {
    const input = fixture.nativeElement.querySelector(selector) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
    input.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
  }
});
