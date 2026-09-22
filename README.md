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

## API de dueños — Sprint 02

La API expone `/api/duenos` para crear y listar dueños, y `/api/duenos/{id}` para consultar, actualizar y eliminar. Devuelve 201 al crear, 200 al consultar o actualizar, 204 al eliminar, 404 si el dueño no existe y 409 si el DNI ya está registrado. Los datos obligatorios ausentes o en blanco devuelven 400.

- [Consigna del sprint 02](docs/sprints/Sprint_02_MVC_REST_CRUD_Dueno.docx).
- [Colección de Postman con pruebas y respuestas de ejemplo](docs/evidencias/sprint-02/sprint-02-duenos.postman_collection.json).
- [Resultados JSON de la colección ejecutada con Newman](docs/evidencias/sprint-02/sprint-02-duenos.newman-results.json).

El DNI se conserva al actualizar. La API de este sprint gestiona los datos del dueño; las mascotas se incorporan en el siguiente sprint.

## Documentación

![Diagrama de dominio de VetSystem](docs/evidencias/sprint-01/sprint-01-diagrama-dominio.png)

- [Fuente PlantUML del diagrama](docs/evidencias/sprint-01/sprint-01-diagrama-dominio.puml)
- [Evidencia de las tablas creadas en H2](docs/evidencias/sprint-01/sprint-01-tablas-h2.png)
- [Documentos PDF y DOCX de los sprints](docs/sprints/README.md)

### Organización de evidencias

Cada sprint guarda sus evidencias en `docs/evidencias/sprint-NN/`, con el número de sprint de dos dígitos (`01`, `02`, `03`, etc.). Los nombres usan minúsculas, sin espacios ni tildes:

- `sprint-NN-tema.postman_collection.json`: colección importable en Postman.
- `sprint-NN-tema.newman-results.json`: exportación JSON original de la ejecución con Newman.
- `sprint-NN-descripcion.ext`: otros entregables del sprint, como el diagrama y la captura de H2 del sprint 01.

Para las pruebas de API se guardan únicamente la colección y el JSON de resultados, sin capturas ni informes adicionales. Se conserva la última ejecución aprobada; las anteriores quedan en el historial de Git.

## Ramas

- `main`: versión estable del proyecto.
- `sprint-01`: trabajo correspondiente al Sprint 1.
- `sprint-02`: arquitectura MVC y CRUD REST de dueños.
