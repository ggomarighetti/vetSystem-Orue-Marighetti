# Análisis del monolito de Patitas Felices

## Estado actual

El sistema reúne dueños, mascotas, veterinarios y turnos en una aplicación Spring Boot. Las capas Controller, Service, Repository y DTO separan responsabilidades, y las validaciones, el manejo centralizado de errores y las pruebas del sprint 06 permiten cambiar el código con una base de verificación. Para el volumen de desarrollo actual, una sola aplicación y H2 en memoria simplifican el arranque y las operaciones. Los problemas siguientes aparecen al exigir escalado, despliegue y evolución independientes; no constituyen por sí solos una razón para migrar sin medir su impacto.

## 1. Escalar solo los turnos

Si la clínica abre reservas en línea y recibe un pico de solicitudes de turnos, el tráfico se concentra en `TurnoController`, `TurnoService` y las consultas de agenda. Como esas clases viven en el mismo proceso que dueños, mascotas y veterinarios, aumentar la capacidad exige iniciar más instancias de toda la aplicación. Cada réplica carga también los otros módulos y sus conexiones a la base, aunque permanezcan casi inactivos. El costo de memoria y CPU crece para funciones que no lo necesitan, y la persistencia compartida puede seguir siendo el cuello de botella. En la Fase 2 se podría extraer Turnos a un servicio con réplicas y capacidad propias; antes habría que definir cómo consulta las identidades de mascotas y veterinarios y medir si la base y las consultas admiten ese tráfico.

## 2. Aislar una falla de Veterinarios

Un bug en el módulo de Veterinarios puede afectar operaciones de otros módulos cuando provoca agotamiento de hilos o conexiones, consumo excesivo de memoria o la caída del proceso Java. Por ejemplo, una consulta defectuosa de profesionales que satura el pool de conexiones impediría también registrar dueños o consultar mascotas, aun cuando su código no haya cambiado. Un error que solo devuelve HTTP 500 en ese endpoint no derriba necesariamente toda la aplicación, pero comparte recursos y despliegue con los demás. En la Fase 2, ejecutar Veterinarios como servicio independiente limitaría el efecto de una caída de su proceso; las llamadas desde Turnos aún necesitarían tiempos de espera, manejo de errores y una política explícita para continuar o rechazar la reserva.

## 3. Usar bases distintas para historial y dueños

Hoy las entidades JPA y los repositorios pertenecen a una misma aplicación, con una fuente de datos H2 en memoria durante el desarrollo. Si el historial de turnos necesitara MongoDB mientras los dueños se guardaran en MySQL, habría que introducir dos configuraciones de persistencia, decidir qué entidades pertenecen a cada almacén y resolver las consultas que hoy pueden apoyarse en relaciones JPA. Una transacción local ya no cubriría cambios repartidos entre ambos motores, y los identificadores de dueño, mascota y veterinario tendrían que mantenerse coherentes sin claves foráneas entre bases. En la Fase 2, cada servicio podría poseer su almacén y exponer contratos para compartir los datos necesarios; eso permitiría elegir tecnología por necesidad, a cambio de sincronización, consistencia eventual cuando corresponda y más observabilidad. La elección de MongoDB debería justificarse por el patrón real de acceso al historial, no solo por la separación de servicios.

## 4. Trabajo paralelo de dos equipos

Dos equipos pueden trabajar en ramas distintas del mismo repositorio, pero comparten el `pom.xml`, las entidades, los DTO, las migraciones y un único ciclo de integración. Por ejemplo, un equipo que cambia la representación de `VeterinarioDTO` puede romper el flujo de creación de turnos que otro equipo desarrolla; ambos necesitan coordinar el contrato y resolver conflictos antes de fusionar. La suite completa y el despliegue también se convierten en puntos comunes de espera. En la Fase 2 se podrían asignar responsabilidades claras por servicio y publicar contratos versionados para que cada equipo evolucione su código y su canal de entrega. Seguirían siendo necesarias pruebas de contrato y coordinación para cambios entre servicios; dividir repositorios por sí solo no elimina las dependencias funcionales.

## 5. Desplegar solo una mejora de Mascotas

Si se corrige el cálculo de la edad de una mascota, hoy se construye y despliega el mismo artefacto que contiene Dueños, Veterinarios y Turnos. El cambio obliga a reiniciar o reemplazar instancias de toda la aplicación y amplía el alcance de una regresión: una configuración equivocada puede afectar a la clínica completa aunque la modificación sea pequeña. Para reducir esa exposición se requiere ejecutar la suite y planificar el reemplazo de las instancias, con especial cuidado si se desea disponibilidad continua. En la Fase 2, un servicio de Mascotas podría tener su propio artefacto, pruebas y despliegue gradual; las interfaces que consumen otros servicios tendrían que conservar compatibilidad mientras conviven versiones distintas.

## Criterio para la Fase 2

La separación tiene sentido si los límites de dominio y las mediciones muestran beneficios suficientes para compensar llamadas de red, fallas parciales, consistencia entre datos y operaciones más complejas. El análisis anterior identifica candidatos y riesgos concretos; antes de migrar conviene medir demanda de turnos, tiempos de respuesta, uso de conexiones, frecuencia de despliegue e incidentes que afecten a varios módulos.
