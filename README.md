# ms-chat · StyleRadar

Microservicio de chat de StyleRadar (paquete **P3: Chat/Notificaciones**, curso DOSW). Permite que un comprador converse con el vendedor de una prenda de segunda mano para coordinar la entrega.

| Dato | Valor |
| --- | --- |
| Puerto | **8089** |
| Requerimientos | SR-RF-40 (iniciar chat con el vendedor), SR-RF-41 (coordinar la entrega por chat) |
| Base de datos | PostgreSQL propia (`chat_db`), esquema con Flyway |
| Tecnología | Java 17, Spring Boot 3.2, Spring Data JPA, OpenFeign, MapStruct, JJWT |
| Arquitectura | Hexagonal (puertos y adaptadores), según la guía oficial del curso |

## Endpoints

Todos exigen `Authorization: Bearer <JWT>` y el rol `COMPRADOR`.

| Método | Ruta | Qué hace | Respuestas |
| --- | --- | --- | --- |
| POST | `/api/v1/segunda-mano/{publicacionId}/conversaciones` | Inicia el chat con el vendedor. Si ya existe, devuelve la misma conversación. | 200, 401, 403, 404, 422, 503 |
| GET | `/api/v1/usuarios/me/conversaciones` | Conversaciones donde participo, como comprador o vendedor. | 200, 401, 403 |
| GET | `/api/v1/conversaciones/{id}/mensajes` | Mensajes de la conversación en orden de envío. Solo participantes. | 200, 401, 403, 404 |
| POST | `/api/v1/conversaciones/{id}/mensajes` | Envía un mensaje de hasta 1.000 caracteres. | 201, 400, 401, 403, 404, 422, 503 |

Públicos: `/actuator/health`, `/swagger-ui.html` y `/v3/api-docs`.

Los errores usan el formato **RFC 7807** (`application/problem+json`):

```json
{
  "type": "https://styleradar.dosw/errores/regla-de-negocio",
  "title": "Regla de negocio incumplida",
  "status": 422,
  "detail": "No puede iniciar un chat sobre su propia publicación",
  "instance": "/api/v1/segunda-mano/10/conversaciones",
  "timestamp": "2026-11-01T15:00:00Z"
}
```

## Arquitectura

```
src/main/java/edu/dosw/styleradar/chat/
├── domain/                     Núcleo puro: sin Spring ni JPA
│   ├── model/                  Conversacion, Mensaje, TextoMensaje (VO), PublicacionResumen
│   ├── event/                  Eventos de dominio (Sprint 2: MensajeEnviadoEvent)
│   ├── exception/              Excepciones de negocio
│   └── port/
│       ├── in/                 Casos de uso (interfaces)
│       └── out/                Repositorios y consulta de publicaciones (interfaces)
├── application/
│   ├── usecase/                *UseCaseImpl (sin @Service: se registran en BeanConfig)
│   ├── dto/request|response|event
│   └── mapper/in|out           MapStruct
└── infrastructure/
    ├── adapter/in/rest/        ChatController + doc/ChatApi (OpenAPI)
    ├── adapter/in/amqp/        Sprint 2: eventos de RabbitMQ
    ├── adapter/in/websocket/   Sprint 3: chat en tiempo real (STOMP)
    ├── adapter/out/persistence/  Entidades JPA, Spring Data y adaptadores
    ├── adapter/out/external/   Cliente Feign del servicio de publicaciones
    ├── adapter/out/security/   Validación del JWT
    ├── adapter/out/messaging/  Sprint 2: publicador de eventos
    ├── config/                 BeanConfig, SecurityConfig, FeignConfig, OpenApiConfig
    └── exception/              GlobalExceptionHandler (RFC 7807)
```

Las pruebas en `src/test` son un espejo de esa estructura:

- **Dominio:** JUnit 5.
- **Casos de uso:** Mockito, sin contexto de Spring.
- **Infraestructura:** slices de Spring e integración con PostgreSQL real en Testcontainers.

### Dependencias con otros microservicios

- **Publicaciones de segunda mano.** Al iniciar un chat y al enviar un mensaje, el servicio consulta la publicación con Feign (`GET /api/v1/segunda-mano/{id}`) y reenvía el JWT del usuario. En el Sprint 1 apunta al monolito; después apuntará a `ms-segunda-vida-donaciones` (puerto 8093) cambiando solo `SEGUNDA_MANO_URL`.
- **Identidad.** El servicio no consulta usuarios: valida el JWT con la misma llave HS256 con la que lo firma el servicio de identidad.

## Configuración

| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `SERVER_PORT` | Puerto del servicio | `8089` |
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/chat_db` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de la base | `chat` / `chat` |
| `JWT_SECRET_BASE64` | **Obligatoria.** La misma llave Base64 (≥ 256 bits) con la que se firman los tokens | — |
| `JWT_ROL_POR_DEFECTO` | Rol que se asume si el token no trae el claim `roles` | `COMPRADOR` |
| `SEGUNDA_MANO_URL` | URL base del servicio de publicaciones | `http://localhost:8080` |
| `SEGUNDA_MANO_CONNECT_TIMEOUT_MS` / `SEGUNDA_MANO_READ_TIMEOUT_MS` | Tiempos límite de Feign | `3000` / `5000` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma | vacío |

## Cómo correrlo

**Pruebas y cobertura** (las pruebas de integración necesitan Docker; sin Docker se omiten):

```bash
mvn clean verify
# Reporte: target/site/jacoco/index.html
```

**Con Docker Compose** (PostgreSQL + servicio):

```bash
cp .env.example .env      # y poner la llave JWT real en JWT_SECRET_BASE64
docker compose up --build
```

**Prueba rápida con cURL** (con un token emitido por el login del monolito):

```bash
TOKEN="<jwt>"
curl -X POST http://localhost:8089/api/v1/segunda-mano/1/conversaciones -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8089/api/v1/conversaciones/1/mensajes \
     -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
     -d '{"texto":"¿Sigue disponible?"}'
curl http://localhost:8089/api/v1/conversaciones/1/mensajes -H "Authorization: Bearer $TOKEN"
```

Swagger: http://localhost:8089/swagger-ui.html

## CI/CD

| Pipeline | Cuándo corre | Qué hace |
| --- | --- | --- |
| `ci-qa.yml` | Push a `main`, `develop` o `feature/**`, y pull requests | Compila, corre las pruebas y exige 85 % de cobertura. En `main`, si `DEPLOY_ENABLED` está activo, publica la imagen y despliega en Azure QA. |
| `ci-prod.yml` | Tag `v*.*.*` | Lo mismo, con aprobación manual en el ambiente `production`. |

Para activar el despliegue en QA:

1. Crear en Azure un App Service para contenedores y una base PostgreSQL para `ms-chat`.
2. En el repositorio, cargar estos secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `AZURE_CREDENTIALS`, `AZURE_WEBAPP_NAME_QA`, `DB_URL_QA`, `DB_USERNAME_QA`, `DB_PASSWORD_QA`, `JWT_SECRET_QA` (la misma llave del monolito en QA) y `SEGUNDA_MANO_URL_QA` (por ejemplo `https://styleradar-backend-qa.azurewebsites.net`).
3. Crear la variable de repositorio `DEPLOY_ENABLED` con el valor `true`.

## Limitaciones conocidas (Sprint 1)

- **El token del monolito no trae roles**, solo el id del usuario. Mientras el servicio de identidad no agregue el claim `roles`, se asume `COMPRADOR` para cualquier token válido. Con el claim, el rol se aplica de verdad.
- **No se revisa si la cuenta está bloqueada**, porque el servicio no tiene acceso a la base de usuarios. Se resolverá con el evento de cambio de estado de cuenta, o con tokens de corta duración.
- **El cierre del chat por publicación retirada** se detecta consultando la publicación al enviar un mensaje. En el Sprint 2 se reemplaza por el evento `PublicacionRetirada`.
- **El chat no es en tiempo real.** El WebSocket llega en el Sprint 3.
