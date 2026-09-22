# Verificaciones del sprint 07

Realizadas el 22 de septiembre de 2026 sobre la rama `sprint-07`, basada en `main` tras incorporar el sprint 06.

| Verificación | Resultado |
| --- | --- |
| `mvnw.cmd clean test` con Java 21 | 16 pruebas aprobadas; 0 fallos y 0 errores. |
| `GET /swagger-ui.html` | HTTP 200; Swagger UI muestra los cuatro grupos: Dueños, Mascotas, Veterinarios y Turnos. |
| `GET /v3/api-docs` | 11 rutas y 21 operaciones, todas con resumen, descripción y respuestas documentadas. Los parámetros declarados muestran descripción y los cinco DTO publicados tienen ejemplos en sus campos. |
| `Try it out` de `GET /api/duenos` en Swagger UI | HTTP 200 y respuesta JSON. |
| GET de `/api/duenos` con origen `http://localhost:5500` | HTTP 200 con `Access-Control-Allow-Origin: *`. |
| Solicitud previa OPTIONS para POST con header `content-type` | HTTP 200 con `Access-Control-Allow-Origin: *` y `Access-Control-Allow-Methods: POST`. |
| Frontend servido en `http://localhost:5500` | La tabla muestra el estado vacío al cargar. Un email inválido muestra el mensaje `email: El email debe tener un formato válido` de la API debajo del formulario. Un alta válida cierra el modal y aparece en la tabla sin recargar la página. |

La prueba de alta se hizo contra H2 en memoria con datos de ejemplo. Esos datos desaparecen al detener la aplicación. Para reproducir el frontend, iniciar primero el backend en el puerto 8080 y servir la carpeta `frontend/` en el puerto 5500.
