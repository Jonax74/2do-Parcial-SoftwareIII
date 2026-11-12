# Clínica Piedra Azul — backend

API REST de Spring Boot 4.1 con PostgreSQL, JWT y autorización por rol.

## Arquitectura hexagonal

- `domain`: modelos, reglas y puertos de salida; no depende de Spring ni JPA.
- `application`: casos de uso; coordina puertos para iniciar sesión, registrar cuentas y listar pacientes.
- `infrastructure/web`: adaptadores de entrada HTTP, DTOs y traducción de errores.
- `infrastructure/persistence/jpa`: entidades y adaptadores PostgreSQL/JPA para los puertos de repositorio.
- `infrastructure/security`: adaptadores BCrypt/JWT y configuración de autorización.
- `infrastructure/config`: composition root que conecta los puertos con sus adaptadores.

Las dependencias apuntan hacia el dominio: el controlador traduce JSON a comandos, el caso de uso
aplica las reglas y llama puertos; los adaptadores concretos resuelven persistencia, contraseñas,
tokens y transacciones.

## Ejecución local

Requiere Java 21 y PostgreSQL. Desde la raíz del repositorio:

```powershell
docker compose up -d db
```

Desde `backend`, genera una clave de desarrollo temporal y configura un administrador inicial:

```powershell
$jwtBytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
$rng.Dispose()
$env:BOOTSTRAP_ADMIN_NAME = "Administración Clínica"
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@piedraazul.test"
$env:BOOTSTRAP_ADMIN_PASSWORD = Read-Host "Contraseña temporal del administrador"
.\mvnw.cmd spring-boot:run
```

La clave JWT cambia si reinicias la terminal; para este entorno de estudio está bien, pero los
tokens anteriores dejarán de ser válidos. No se guarda una clave ni una contraseña de administrador
en el repositorio. La cuenta inicial se crea una sola vez cuando no existe; no se restablece su
contraseña en siguientes arranques.

La API escucha en `http://localhost:8080`. La conexión se puede cambiar mediante `DB_URL`,
`DB_USERNAME` y `DB_PASSWORD`.

## Endpoints de autenticación y registro

| Método y ruta | Acceso | Descripción |
| --- | --- | --- |
| `POST /api/auth/login` | Público | Valida credenciales y entrega el JWT |
| `GET /api/medicos/publico` | Público | Lista nombre, especialidad y contacto de médicos |
| `POST /api/admin/medicos` | Administrador | Registra un médico |
| `GET /api/admin/medicos` | Administrador | Lista médicos para administración |
| `POST /api/admin/pacientes` | Administrador | Registra un paciente |
| `GET /api/admin/pacientes/existe?documento=...` | Administrador | Comprueba si una cédula ya está registrada |
| `GET /api/admin/citas` | Administrador | Lista todas las citas |
| `POST /api/admin/citas` | Administrador | Crea una cita para un paciente y médico existentes |
| `GET /api/paciente/citas` | Paciente | Lista las citas del usuario autenticado |
| `POST /api/admin/usuarios` | Administrador | Registra otro administrador o un paciente |
| `GET /api/admin/pacientes` | Administrador | Devuelve el listado administrativo |

Las citas programadas deben tener al menos 30 minutos de separación para el mismo paciente y
para el mismo médico. Una diferencia exacta de 30 minutos está permitida; las citas canceladas no
bloquean el horario.

El registro de pacientes requiere una sesión administrativa y siempre asigna el rol `PACIENTE`.
La creación de administradores requiere una sesión administrativa. Para crear el primero, se usa
el bootstrap por variables de entorno. Los correos se normalizan y se rechazan duplicados; la
contraseña se almacena con BCrypt. Un login incorrecto responde `401`; el acceso administrativo
sin token responde `401` y con rol de paciente responde `403`.

## Pruebas

```powershell
.\mvnw.cmd test
```

La suite incluye pruebas unitarias de casos de uso, autorización de rutas y arranque con H2 aislado.
