# Cosmic Horror RPG

Una experiencia de rol narrativo de terror cósmico en la que cada decisión deja huella.

Cosmic Horror RPG combina una interfaz Angular con un backend Java 21 y una arquitectura hexagonal preparada para trabajar con proveedores de IA locales, simulados o remotos. El objetivo no es encadenar textos atmosféricos, sino dirigir aventuras con campaña, personaje, objetivos, pistas, cordura, tiradas de habilidad y un estado de mundo verificable.

La aplicación interpreta las acciones del jugador, conserva la continuidad entre turnos y valida las transiciones narrativas para evitar contradicciones, información imposible o soluciones arbitrarias. La IA propone; el dominio comprueba qué puede cambiar realmente en la ficción.

> Explora lo imposible. Decide qué estás dispuesto a perder.

## Estado del proyecto

Primer MVP funcional en evolución, con flujo de campaña, personaje, sesión narrativa, proveedor simulado, integración OpenAI-compatible, pistas, estado narrativo y pruebas automatizadas.

## Tecnologías

- Java 21 y Spring Boot
- Angular 17 y TypeScript
- Arquitectura hexagonal y DDD pragmático
- Persistencia en memoria o PostgreSQL con Flyway
- JUnit 5 y tests de contrato de la narrativa
- Proveedores de IA intercambiables mediante configuración

## Puesta en marcha

Backend:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
mvn spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm start
```

Por defecto, la API se sirve en `http://localhost:8090` y Angular en `http://localhost:4200`.

Para ejecutar la aplicación sin llamadas externas, utiliza el proveedor simulado:

```text
AI_PROVIDER=stub
```

Para activar un proveedor OpenAI-compatible, define la clave únicamente en el entorno:

```text
AI_PROVIDER=openai
AZURE_OPENAI_API_KEY=<clave-no-guardar-en-el-repositorio>
AI_OPENAI_BASE_URL=https://aiproxy.ernidev.com/v1
```

## API principal

- `POST /api/campaigns`
- `POST /api/campaigns/{campaignId}/characters`
- `POST /api/sessions`
- `GET /api/sessions/{sessionId}`
- `POST /api/sessions/{sessionId}/checks`
- `POST /api/sessions/{sessionId}/turns`

## Dirección del proyecto

La arquitectura está diseñada para crecer desde un único flujo vertical hacia un director narrativo más sofisticado: agentes especializados, memoria de eventos, amenazas con percepción limitada y tensión narrativa, sin convertir prematuramente cada responsabilidad en un microservicio.
