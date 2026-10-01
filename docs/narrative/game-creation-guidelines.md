# Contrato narrativo para creación de partidas

game-creation-skills.yaml (contrato cosmic-horror-narrative, versión 2) es la fuente de reglas compartida por el motor, los prompts y los tests. La versión es compatible con la versión 1 y no contiene una ambientación concreta: Innsmouth, un observatorio o una expedición polar utilizan el mismo contrato.

## Principio central

La IA genera prosa; el motor conserva la verdad de la ficción. La IA no puede crear, mover, eliminar o resolver entidades fuera de una transición válida del estado narrativo.

Los proveedores comparten dos objetos: NarrativeState, que describe la situación al comenzar el turno, y NarrativeTransition, que declara el cambio observable que proponen. El validador exige que la transición parta de la localización actual, que una localización nueva esté establecida por un hecho y que toda entidad introducida tenga causa. Un proveedor puede omitir la transición cuando no hay cambio de estado; no puede inventarlo en la prosa.

Cada turno debe seguir esta cadena:

`intención del jugador → reacción del mundo → consecuencia observable → transición de estado → decisiones significativas`

## Estado mínimo

Una sesión debe conocer la localización actual, entidades presentes, hechos canónicos, pistas, objetivo, consecuencias pendientes y reloj de tensión. El historial textual por sí solo no es un modelo de estado fiable.

## Acciones

Las acciones se interpretan semánticamente antes de llegar al proveedor. No se deben activar ramas mediante subcadenas simples: no abrir la puerta no es abrir la puerta. La interpretación conserva intención, negación, incertidumbre y cláusulas múltiples; una acción negada no puede producir el efecto de la acción afirmativa.

## Criterios de aceptación

Una propuesta narrativa se rechaza si contradice hechos, introduce entidades sin causa, repite el contexto, no reconoce la acción o cierra un objetivo sin resolverlo. La coherencia, atmósfera y agencia deben alcanzar al menos 7/10.

## Pruebas cruzadas

Las pruebas deben utilizar campañas con vocabularios distintos y comprobar que no aparecen elementos heredados de plantillas ajenas. La matriz mínima cubre Innsmouth, un observatorio y una expedición polar, además de una negación y una transición inválida.
