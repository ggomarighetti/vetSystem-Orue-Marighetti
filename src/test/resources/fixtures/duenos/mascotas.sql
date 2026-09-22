INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, dueno_id)
SELECT 'Luna', 'Perro', 'Labrador', DATE '2020-05-10', id
FROM duenos WHERE dni = '28543210';

INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, dueno_id)
SELECT 'Milo', 'Gato', 'Siamés', DATE '2022-03-15', id
FROM duenos WHERE dni = '28543210';
