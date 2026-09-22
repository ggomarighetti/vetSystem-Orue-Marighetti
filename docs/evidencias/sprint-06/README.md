# Evidencia del sprint 06

La suite del sprint cubre 16 casos: seis de `DuenoServiceTest`, dos de `TurnoServiceTest` y ocho de `DuenoControllerTest`, incluidos cuatro casos parametrizados de campos obligatorios en blanco. El proyecto usa `spring-boot-starter-test` para JUnit y Mockito, y `spring-boot-starter-webmvc-test` para MockMvc.

| Verificación | Resultado |
| --- | --- |
| `.\mvnw.cmd clean test` | `BUILD SUCCESS`; 16 pruebas, 0 fallos, 0 errores, 0 omitidas. La ejecución limpia compiló cuatro clases de prueba y no inició JPA ni H2. |
| `.\mvnw.cmd test "-Dsurefire.runOrder=random"` | `BUILD SUCCESS`; 16 pruebas, 0 fallos, 0 errores, 0 omitidas. |
| Mutación temporal de `DuenoService.createDueno` para omitir la consulta de DNI duplicado | `createDueno_cuandoDniDuplicado_lanzaDuplicateResourceException` falló como se esperaba: recibió `NullPointerException` en lugar de `DuplicateResourceException`. Se restauró la implementación original. |

La prueba web usa `@WebMvcTest` y servicios simulados con `@MockitoBean`. Las pruebas de servicios usan `@ExtendWith(MockitoExtension.class)`, repositorios y mappers simulados. Ninguna prueba usa `@SpringBootTest` ni una base de datos real.

La consigna menciona `@MockBean` y `TurnoSuperpuestoException`; la implementación actual de Spring Boot 4.1 y la regla del repositorio de reutilizar excepciones requieren `@MockitoBean` y `DuplicateResourceException`, respectivamente. La regla del repositorio de no agregar comentarios al código fuente prevalece sobre el pedido de marcar las fases AAA con comentarios.
