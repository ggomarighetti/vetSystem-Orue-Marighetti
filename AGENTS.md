# Reglas del proyecto

## Código fuente sin comentarios

- No escribir comentarios en los archivos de código fuente del proyecto. Esta regla se aplica a todos los sprints, al código de producción, a las pruebas y a la configuración dentro de `src/`, así como a nuevos archivos de código ubicados fuera de esa carpeta.
- No agregar comentarios de línea, comentarios de bloque, Javadoc, TODO/FIXME ni código comentado.
- Expresar la intención mediante nombres claros y métodos pequeños. Si hace falta una explicación adicional, escribirla en el README o en la documentación fuera de los archivos de código fuente.
- Al modificar código, mantener esta regla y comprobar que no se hayan introducido comentarios.

## Formato de las pull requests

- Todas las descripciones de PR deben contener, en este orden, los encabezados exactos `### Resume`, `### Evidence` y `### Reference`.
- En `Resume`, resumir el problema resuelto y el comportamiento final de los cambios.
- En `Evidence`, indicar las verificaciones realizadas y sus resultados, con enlaces a las evidencias disponibles. No afirmar que se ejecutaron pruebas que no se hayan realizado.
- En `Reference`, enlazar la consigna del sprint, los issues o la documentación que fundamenta el cambio. No inventar referencias; si no hay ninguna, indicarlo.
- Aplicar este formato al crear y al actualizar una PR, conservando la información relevante y revisando que describa el alcance final.
- Usar `.github/pull_request_template.md` como base y reemplazar sus indicaciones por el contenido concreto de la PR.
