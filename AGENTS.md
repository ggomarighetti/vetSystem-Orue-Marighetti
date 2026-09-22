# Reglas del proyecto

## Código fuente sin comentarios

- No escribir comentarios en los archivos de código fuente del proyecto. Esta regla se aplica a todos los sprints, al código de producción y a las pruebas, así como a nuevos archivos de código ubicados fuera de `src/`.
- Los archivos de configuración, como `src/main/resources/application.properties`, pueden contener comentarios explicativos.
- No agregar comentarios de línea, comentarios de bloque, Javadoc, TODO/FIXME ni código comentado.
- Expresar la intención mediante nombres claros y métodos pequeños. Si hace falta una explicación adicional, escribirla en el README o en la documentación fuera de los archivos de código fuente.
- Al modificar código, mantener esta regla y comprobar que no se hayan introducido comentarios.

## Excepciones reutilizables

- Reutilizar excepciones por categoría de error, sin crear una clase por entidad, campo o caso concreto.
- Las excepciones deben recibir el mensaje desde el lugar donde se lanzan. No fijar en sus clases mensajes de negocio ni exigir datos como DNI o identificadores de un tipo concreto.
- Al traducir una excepción, conservar la causa original. El mapeo a respuestas HTTP debe permanecer centralizado en `config/GlobalExceptionHandler.java`.

## Pruebas de integración

- Usar `@ApiIntegrationTest` para las pruebas de integración de la API. La configuración común pertenece a `src/test/resources/application-test.properties`, sin repetir propiedades en cada clase.
- Preparar los datos persistidos mediante scripts de `src/test/resources/fixtures/` seleccionados con `@Sql` en la clase o el método. Mantener `@SqlMergeMode(MERGE)` para combinar ambos niveles.
- La limpieza antes y después de cada prueba se gestiona en `DatabaseCleanupListener`, con orden anterior a la carga de fixtures y transacciones independientes. No agregar limpieza manual con `@BeforeEach` ni envolver estos tests de API en `@Transactional`.
- Las clases que comparten la base de integración deben conservar el bloqueo `api-database` de la anotación común. La concurrencia dentro de un caso se prueba explícitamente y debe finalizar antes de salir del método.
- Construir las solicitudes con objetos nuevos de `DuenoTestData` y serializarlas con el Jackson de Spring. Agrupar las comprobaciones directas de la base en `DatabaseAssertions`; mantener las consultas de preparación y limpieza fuera de los tests de API.
- Al agregar entidades o relaciones, actualizar `fixtures/cleanup.sql` respetando las claves foráneas y las verificaciones de conservación de datos.

## Formato de las pull requests

- Todas las descripciones de PR deben contener, en este orden, los encabezados exactos `### Resume`, `### Evidence` y `### Reference`.
- En `Resume`, resumir el problema resuelto y el comportamiento final de los cambios.
- En `Evidence`, indicar las verificaciones realizadas y sus resultados, con enlaces a las evidencias disponibles. No afirmar que se ejecutaron pruebas que no se hayan realizado.
- En `Reference`, enlazar la consigna del sprint, los issues o la documentación que fundamenta el cambio. No inventar referencias; si no hay ninguna, indicarlo.
- Aplicar este formato al crear y al actualizar una PR, conservando la información relevante y revisando que describa el alcance final.
- Usar `.github/pull_request_template.md` como base y reemplazar sus indicaciones por el contenido concreto de la PR.
