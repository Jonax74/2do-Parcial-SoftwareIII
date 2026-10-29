# Clínica Piedra Azul — frontend

Frontend Angular 22 para el sistema de la Clínica Piedra Azul.

## Estructura Atomic Design

- `shared/atoms`: controles pequeños reutilizables; `data-table` es la tabla genérica.
- `shared/molecules`: composición de controles; `table-section` reúne título y tabla.
- `shared/organisms`: `patient-list` obtiene y muestra el directorio correspondiente.
- `shared/templates`: `app-shell` aporta encabezado, navegación y pie adaptables.
- `pages`: inicio de sesión y listados público y administrativo.
- `core`: autenticación, sesión, guards, interceptor y acceso a la API.

## Ejecución local

El backend debe estar disponible en `http://localhost:8080` y la base de datos configurada.
Desde esta carpeta:

```powershell
npm ci
npm start
```

La aplicación se sirve en `http://localhost:4200`. El inicio de sesión requiere un usuario
existente; el registro de cuentas se incorporará en una etapa posterior.

## Rutas de esta etapa

| Ruta | Acceso |
| --- | --- |
| `/login` | Público |
| `/pacientes` | Público; solo muestra nombre y documento |
| `/admin/pacientes` | Administrador; requiere sesión y rol |

El token se conserva en `sessionStorage`. El interceptor lo envía a las rutas privadas de la API;
la autorización definitiva la aplica el backend.

## Validación

```powershell
npm test -- --watch=false
npm run build
```
