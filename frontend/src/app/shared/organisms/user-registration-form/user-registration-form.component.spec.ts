import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { PatientService } from '../../../core/patients/patient.service';
import { UserRegistrationFormComponent } from './user-registration-form.component';

describe('UserRegistrationFormComponent', () => {
  let fixture: ComponentFixture<UserRegistrationFormComponent>;
  let auth: {
    registerPatient: ReturnType<typeof vi.fn>;
    registerUser: ReturnType<typeof vi.fn>;
  };
  let patients: { documentExists: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    auth = {
      registerPatient: vi.fn().mockReturnValue(
        of({ id: 1, nombre: 'Ana Pérez', correo: 'ana@example.com', rol: 'PACIENTE' }),
      ),
      registerUser: vi.fn().mockReturnValue(
        of({ id: 2, nombre: 'Admin', correo: 'admin@example.com', rol: 'ADMINISTRADOR' }),
      ),
    };
    patients = { documentExists: vi.fn().mockReturnValue(of(false)) };

    await TestBed.configureTestingModule({
      imports: [UserRegistrationFormComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: auth },
        { provide: PatientService, useValue: patients },
      ],
    }).compileComponents();
  });

  it('supports registering a patient from the administrator area', () => {
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.componentRef.setInput('adminMode', true);
    fixture.componentRef.setInput('patientOnly', true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#rol')).toBeNull();
    expect(fixture.nativeElement.querySelector('#documento')).toBeTruthy();
  });

  it('submits an administrator-created patient after checking their document', async () => {
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.componentRef.setInput('adminMode', true);
    fixture.componentRef.setInput('patientOnly', true);
    fixture.detectChanges();

    setInput('#nombre', 'Ana Pérez');
    setInput('#registro-correo', 'ana@example.com');
    setInput('#registro-password', 'password123');
    setInput('#documento', '123456');
    await fixture.whenStable();
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(patients.documentExists).toHaveBeenCalledWith('123456');
    expect(auth.registerPatient).toHaveBeenCalledWith(
      expect.objectContaining({
        nombre: 'Ana Pérez',
        correo: 'ana@example.com',
        documento: '123456',
      }),
    );
    expect(fixture.nativeElement.textContent).toContain('se registró correctamente');
  });

  it('blocks registration when the document is already registered', async () => {
    patients.documentExists.mockReturnValue(of(true));
    fixture = TestBed.createComponent(UserRegistrationFormComponent);
    fixture.componentRef.setInput('adminMode', true);
    fixture.componentRef.setInput('patientOnly', true);
    fixture.detectChanges();
    setInput('#documento', '123456');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Ya existe un paciente registrado');
    expect(fixture.componentInstance['form'].invalid).toBe(true);
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
