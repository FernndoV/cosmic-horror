# ADR-002: IA detrás de un puerto

El dominio recibe propuestas estructuradas mediante `NarrativeGenerationPort`. Los SDK, prompts y credenciales pertenecen a adaptadores. El proveedor simulado es obligatorio en tests.

El proveedor inicial será OpenAI mediante LangChain4j. Se activa con AI_PROVIDER=openai; el valor por defecto seguirá siendo stub para que los tests no dependan de un proceso externo. La respuesta se solicita como JSON y se valida antes de entrar en el dominio.
