# Sistema Bancario - Backend

API REST de un banco basico hecha con **Spring Boot 4.1 + Spring Security + JWT**, documentada con **Swagger**
y con base de datos **H2**. Proyecto de practica de programacion web.

Frontend (Angular + Tailwind): https://github.com/dast2003/sistema-bancario-angular-frontentendn

## Funcionalidades

- Registro de personas (nombre, apellido, documento, email, contrasena). Al registrarse se abre una cuenta de ahorros.
- Login con email y contrasena: devuelve un **JWT** valido por 1 hora.
- Consultar cuentas, abrir cuentas nuevas (ahorros o corriente, maximo 5).
- Depositar, retirar (valida saldo) y transferir a cualquier cuenta del banco.
- Historial de movimientos por cuenta.

## Ejecutar

Requisitos: Java 21 y Maven (o el wrapper `mvnw` incluido).

```bash
mvn spring-boot:run
```

| Recurso      | URL                                   |
|--------------|---------------------------------------|
| Swagger UI   | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs     |
| Consola H2   | http://localhost:8080/h2-console      |

Consola H2: JDBC URL `jdbc:h2:file:./data/bancodb`, usuario `sa`, sin contrasena.

## Probar en Swagger

1. `POST /api/auth/registro` o `POST /api/auth/login` y copia el `token` de la respuesta.
2. Clic en **Authorize** (candado) y pega el token (sin la palabra `Bearer`).
3. Ya puedes llamar los endpoints de cuentas y transferencias.

## Endpoints

| Metodo | Ruta                                | Auth | Descripcion                            |
|--------|-------------------------------------|------|----------------------------------------|
| POST   | `/api/auth/registro`                | No   | Registrar persona                      |
| POST   | `/api/auth/login`                   | No   | Iniciar sesion                         |
| GET    | `/api/usuarios/me`                  | JWT  | Mi perfil                              |
| GET    | `/api/cuentas`                      | JWT  | Mis cuentas                            |
| POST   | `/api/cuentas`                      | JWT  | Abrir cuenta `{ "tipo": "CORRIENTE" }` |
| POST   | `/api/cuentas/{numero}/depositos`   | JWT  | Depositar `{ "monto": 50000 }`         |
| POST   | `/api/cuentas/{numero}/retiros`     | JWT  | Retirar                                |
| GET    | `/api/cuentas/{numero}/movimientos` | JWT  | Historial                              |
| POST   | `/api/transferencias`               | JWT  | Transferir                             |

Codigos de error: `400` datos invalidos · `401` sin token o credenciales malas · `404` cuenta no encontrada o ajena ·
`409` email/documento duplicado · `422` saldo insuficiente.

## Seguridad

- Contrasenas guardadas con **BCrypt**.
- API **stateless**: cada peticion lleva `Authorization: Bearer <token>`, validado por `JwtAuthenticationFilter`.
- Una persona solo puede operar sus propias cuentas (una ajena responde 404).
- CORS habilitado solo para `http://localhost:4200`.
- `@Version` en `Cuenta` (bloqueo optimista) evita que dos operaciones simultaneas pisen el saldo.
- El secreto JWT de `application.properties` es solo para desarrollo; en otro entorno define la variable `JWT_SECRET`.

## Estructura

```
com.banco
├── config/       SecurityConfig (seguridad, CORS, BCrypt), OpenApiConfig (Swagger + JWT)
├── controller/   AuthController, UsuarioController, CuentaController, TransferenciaController
├── dto/          Records de peticion/respuesta con validaciones
├── entity/       Usuario, Cuenta, Transaccion y enums
├── exception/    Excepciones de negocio y GlobalExceptionHandler
├── repository/   Spring Data JPA
├── security/     JwtService, JwtAuthenticationFilter, UsuarioDetailsService
└── service/      AuthService, CuentaService
```

## Tests

```bash
mvn test
```

Pruebas de integracion: registro, login, deposito, retiro sin saldo, transferencia, acceso a cuenta ajena,
rutas sin token, credenciales incorrectas, datos duplicados/invalidos y Swagger publico.
