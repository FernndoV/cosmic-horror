# Cosmic Horror RPG

Primer vertical slice de una aplicación narrativa de terror cósmico.

## Ejecutar backend

Requiere JDK 21. `mvn spring-boot:run` inicia la API en `http://localhost:8080`.

## API mínima

- `POST /api/sessions`
- `GET /api/sessions/{sessionId}`
- `POST /api/sessions/{sessionId}/turns`

El proveedor OpenAI está configurado de forma estática. Define `AZURE_OPENAI_API_KEY` en el entorno; el modelo utilizado es `gpt-4o-mini`.
