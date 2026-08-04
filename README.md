# VetSystem - Clínica Veterinaria Patitas Felices

Sistema de gestión para la Clínica Veterinaria **Patitas Felices**, desarrollado como trabajo práctico de la materia Microservicios y APIs Escalables de la Universidad de Palermo.

## Integrante

- Guillermo Gabriel Orue Marighetti

## Cómo levantar el proyecto

### Requisitos

- Java 21
- No es necesario instalar Maven: el repositorio incluye Maven Wrapper.

Mientras la conexión a MySQL no esté configurada (punto 3 del Sprint 1), el proyecto desactiva temporalmente la autoconfiguración del datasource para poder verificar el arranque inicial.

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

Para ejecutar las pruebas:

```powershell
.\mvnw.cmd test
```

## Ramas

- `main`: versión estable del proyecto.
- `sprint-01`: trabajo correspondiente al Sprint 1.
