# Clínica Piedra Azul — frontend

Frontend Angular 22 para el sistema de la Clínica Piedra Azul.


## Estructura Atomic Design

- `shared/atoms`: controles pequeños reutilizables; `data-table` es la tabla genérica.
- `shared/molecules`: `table-section` reúne encabezado, contador y tabla.
- `shared/organisms`: directorio médico, listado de pacientes, formularios y agenda de citas.
- `shared/templates`: `app-shell` aporta encabezado, navegación y pie adaptables.
- `pages`: vistas públicas, administrativas y del paciente.
- `core`: modelos, acceso a API, autenticación, sesión, guards e interceptor.

Los formularios de médico, paciente y cita usan formularios reactivos con validaciones. El registro
administrativo de pacientes consulta el backend al salir del campo de cédula y bloquea duplicados.

## Ejecución local

El backend debe estar disponible en `http://localhost:8080` y la base de datos configurada.
Desde esta carpeta:

```powershell
npm ci
npm start
```

La aplicación se sirve en `http://localhost:4200`. El primer administrador se configura mediante
las variables de entorno documentadas en `backend/README.md`; no hay autorregistro público.

## Rutas

| Ruta | Acceso |
| --- | --- |
| `/medicos` | Público; directorio médico |
| `/login` | Público |
| `/admin/medicos` | Administrador; registro y listado |
| `/admin/pacientes` | Administrador; listado |
| `/admin/pacientes/nuevo` | Administrador; formulario reactivo de registro |
| `/admin/citas` | Administrador; creación y listado global |
| `/paciente/citas` | Paciente; solo sus propias citas |

El token se conserva en `sessionStorage`. El interceptor lo envía a las rutas privadas de la API;
la autorización definitiva la aplica el backend.

## Validación

```powershell
npm test -- --watch=false
npm run build
```
