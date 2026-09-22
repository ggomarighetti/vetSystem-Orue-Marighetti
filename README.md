# VetSystem - Clínica Veterinaria Patitas Felices

Sistema de gestión para la Clínica Veterinaria **Patitas Felices**, desarrollado como trabajo práctico de la materia Microservicios y APIs Escalables de la Universidad de Palermo.

## Integrante

- Guillermo Gabriel Orue Marighetti

## Cómo levantar el proyecto

### Requisitos

- Java 21
- No es necesario instalar Maven: el repositorio incluye Maven Wrapper.
- No es necesario instalar un servidor de base de datos: se utiliza H2 en memoria.

La base de datos se crea al iniciar la aplicación y se elimina al detenerla.

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

### Consola H2

La consola solo está disponible con el perfil `dev`. Para iniciar la aplicación con ese perfil:

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

En Linux o macOS:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Luego, abrir `http://localhost:8080/h2-console` y usar:

- JDBC URL: `jdbc:h2:mem:vet_system`
- Usuario: `sa`
- Contraseña: dejar vacía

Sin el perfil `dev`, la consola H2 permanece deshabilitada.

Esto solo limita el acceso a la consola; la base de datos sigue siendo H2 en memoria en todos los perfiles.

> **Nota - Historia 3:** usamos H2 en memoria en lugar de MySQL. Es una configuración de desarrollo y los datos se reinician al detener la aplicación.

Para ejecutar las pruebas:

```powershell
.\mvnw.cmd test
```

### Organización de las pruebas

Las pruebas de integración usan `@ApiIntegrationTest`, que reúne el contexto de Spring Boot, MockMvc, el perfil `test` y las utilidades comunes. La configuración está en `src/test/resources/application-test.properties`: H2 en memoria para pruebas, esquema creado por Hibernate al cargar el contexto y configuración JPA compartida.

Los escenarios se preparan con `@Sql` y los archivos de `src/test/resources/fixtures/`. La anotación puede colocarse en una clase para cargar datos antes de cada método o en un método para agregar datos específicos. `@SqlMergeMode(MERGE)` combina ambos niveles. Por ejemplo, `DuenoRelacionesApiTests` carga el dueño y sus mascotas para todos sus casos, y el test con turno agrega `fixtures/duenos/turno.sql`.

`DatabaseCleanupListener` ejecuta `fixtures/cleanup.sql` antes y después de cada caso, en transacciones independientes. Su orden garantiza que la limpieza inicial ocurra antes de los scripts de `@Sql`. Los borrados respetan las claves foráneas y los datos de prueba usan IDs generados por la base. Al agregar tablas, también debe actualizarse el script de limpieza.

Las pruebas de API conservan las transacciones del servicio para verificar commits, conflictos y concurrencia. La anotación común incluye el bloqueo `api-database`, que evita que distintas pruebas modifiquen simultáneamente la base compartida cuando se habilita la ejecución paralela de JUnit. El caso de altas concurrentes sigue ejecutando sus solicitudes en varios hilos dentro de una única prueba.

`DuenoTestData` construye objetos nuevos para las solicitudes que se serializan con Jackson. `DatabaseAssertions` agrupa las consultas de verificación y compara el estado completo de dueños, mascotas, turnos y veterinarios después de un borrado rechazado.

## API de dueños — Sprint 02

La API expone `/api/duenos` para crear y listar dueños, y `/api/duenos/{id}` para consultar, actualizar y eliminar. Devuelve 201 al crear, 200 al consultar o actualizar, 204 al eliminar un dueño sin mascotas, 404 si el dueño no existe y 409 si el DNI ya está registrado o se intenta eliminar un dueño con mascotas asociadas. Los datos obligatorios ausentes o en blanco devuelven 400.

El DELETE conserva las dependencias: si el dueño tiene mascotas, con o sin turnos, responde 409 con un mensaje descriptivo y mantiene intactos el dueño, las mascotas y los turnos. El borrado no se propaga en cascada; primero deben resolverse las asociaciones mediante las operaciones correspondientes de mascotas.

- [Consigna del sprint 02](docs/sprints/Sprint_02_MVC_REST_CRUD_Dueno.docx).
- [Colección de Postman con pruebas y respuestas de ejemplo](docs/evidencias/sprint-02/sprint-02-duenos.postman_collection.json).
- [Resumen JSON de resultados de la colección ejecutada con Newman](docs/evidencias/sprint-02/sprint-02-duenos.newman-results.json).

El DNI se conserva al actualizar. La API de este sprint gestiona los datos del dueño; las mascotas se incorporan en el siguiente sprint.

## Documentación

![Diagrama de dominio de VetSystem](docs/evidencias/sprint-01/sprint-01-diagrama-dominio.png)

- [Fuente PlantUML del diagrama](docs/evidencias/sprint-01/sprint-01-diagrama-dominio.puml)
- [Evidencia de las tablas creadas en H2](docs/evidencias/sprint-01/sprint-01-tablas-h2.png)
- [Documentos PDF y DOCX de los sprints](docs/sprints/README.md)

### Organización de evidencias

Cada sprint guarda sus evidencias en `docs/evidencias/sprint-NN/`, con el número de sprint de dos dígitos (`01`, `02`, `03`, etc.). Los nombres usan minúsculas, sin espacios ni tildes:

- `sprint-NN-tema.postman_collection.json`: colección importable en Postman.
- `sprint-NN-tema.newman-results.json`: resumen JSON derivado de la ejecución con Newman.
- `sprint-NN-descripcion.ext`: otros entregables del sprint, como el diagrama y la captura de H2 del sprint 01.

Para las pruebas de API se guardan únicamente la colección y el JSON de resultados, sin capturas ni informes adicionales. Se conserva la última ejecución aprobada; las anteriores quedan en el historial de Git.

El resumen usa `formatVersion: 1` e incluye herramienta de origen, fecha de ejecución, nombre de la colección, totales y fallos. Cada elemento de `results` conserva nombre, método, URL, estado HTTP, cuerpo de respuesta JSON (`null` si no hay cuerpo) y resultado de cada verificación. Es un formato propio: omite la colección duplicada y los detalles internos del reporte completo de Newman.

## Ramas

- `main`: versión estable del proyecto.
- `sprint-01`: trabajo correspondiente al Sprint 1.
- `sprint-02`: arquitectura MVC y CRUD REST de dueños.
