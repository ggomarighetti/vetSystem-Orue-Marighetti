# Análisis del monolito de Patitas Felices

La aplicación funciona como un monolito Spring Boot: reúne dueños, mascotas, veterinarios y turnos en un solo proyecto. Para este trabajo universitario, esa estructura es suficiente y facilita ejecutar y probar el sistema con H2 en memoria. Los cinco casos siguientes son situaciones hipotéticas para entender por qué en la Fase 2 estudiaremos microservicios. El tamaño actual del proyecto se resuelve bien con el monolito.

## 1. Escalar solo Turnos

Si muchos clientes intentaran reservar turnos al mismo tiempo, el módulo de Turnos recibiría más carga que los demás. Hoy se ejecuta junto con Dueños, Mascotas y Veterinarios en la misma aplicación; para aumentar la capacidad de Turnos habría que ejecutar más copias del sistema completo. Eso también consumiría recursos para módulos cuya demanda no cambió. En la Fase 2, separar Turnos permitiría ampliar solo ese servicio cuando hiciera falta.

## 2. Falla en Veterinarios

Si un error en Veterinarios hiciera fallar toda la aplicación, los clientes tampoco podrían consultar dueños, mascotas o turnos, porque todos los módulos comparten el mismo proceso. Por ejemplo, un fallo al iniciar la parte de Veterinarios podría impedir que arranque el sistema completo. Un error aislado en una solicitud de Veterinarios no necesariamente detiene los demás endpoints, pero muestra que comparten el mismo despliegue. En la Fase 2, un servicio separado limitaría el efecto de una caída de Veterinarios; Turnos todavía tendría que manejar el caso en que necesite datos de ese servicio y no estén disponibles.

## 3. Bases de datos distintas

Hoy los repositorios JPA usan una sola base H2 en memoria. Si quisiéramos guardar el historial de turnos en MongoDB y los dueños en MySQL, habría que configurar dos formas de persistencia dentro de la misma aplicación y revisar cómo se relacionan los datos. Por ejemplo, un turno guarda referencias a una mascota y a un veterinario; esas relaciones no podrían depender de una misma base si se separan los datos. En la Fase 2, cada servicio podría administrar su propia base y comunicar los datos necesarios mediante su API. El uso de MongoDB y MySQL es un supuesto de la consigna, no una necesidad actual del proyecto.

## 4. Dos equipos en paralelo

Dos equipos podrían usar ramas distintas, pero seguirían modificando un mismo proyecto. Si un equipo cambia `VeterinarioDTO` mientras otro trabaja en Turnos, ambos deben coordinar el formato de los datos y resolver posibles conflictos al unir sus cambios. Un cambio compartido también exige volver a probar la aplicación completa. En la Fase 2, cada equipo podría encargarse de un servicio con código y entregas propios; aun así, tendrían que acordar las API que conectan los servicios.

## 5. Actualizar solo Mascotas

Si se corrigiera la validación de la fecha de nacimiento de una mascota, hoy habría que construir y desplegar toda la aplicación, aunque Dueños, Veterinarios y Turnos no hubieran cambiado. Eso amplía el alcance de una modificación pequeña y obliga a verificar que el resto siga funcionando. En la Fase 2, un servicio de Mascotas permitiría desplegar esa corrección por separado. Los otros servicios seguirían usando su API, por lo que cualquier cambio en ella debería coordinarse.
