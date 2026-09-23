# Contrato narrativo para creación de partidas

`game-creation-skills.yaml` es la fuente de reglas compartida por el motor, los prompts y los tests. No contiene una ambientación concreta: Innsmouth, un observatorio o una expedición polar deben utilizar el mismo contrato.

## Principio central

La IA genera prosa; el motor conserva la verdad de la ficción. La IA no puede crear, mover, eliminar o resolver entidades fuera de una transición válida del estado narrativo.

Cada turno debe seguir esta cadena:

`intención del jugador → reacción del mundo → consecuencia observable → transición de estado → decisiones significativas`

## Estado mínimo

Una sesión debe conocer la localización actual, entidades presentes, hechos canónicos, pistas, objetivo, consecuencias pendientes y reloj de tensión. El historial textual por sí solo no es un modelo de estado fiable.

## Acciones

Las acciones se interpretan semánticamente. No se deben activar ramas mediante subcadenas simples: `no abrir la puerta` no es `abrir la puerta`. La interpretación debe conservar negaciones, incertidumbre y acciones múltiples.

## Criterios de aceptación

Una propuesta narrativa se rechaza si contradice hechos, introduce entidades sin causa, repite el contexto, no reconoce la acción o cierra un objetivo sin resolverlo. La coherencia, atmósfera y agencia deben alcanzar al menos 7/10.

## Pruebas cruzadas

Las pruebas deben utilizar campañas con vocabularios distintos y comprobar que no aparecen elementos heredados de plantillas ajenas. Como mínimo se recomienda cubrir un puerto, un observatorio y una expedición polar.
