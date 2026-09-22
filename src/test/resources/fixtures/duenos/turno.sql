INSERT INTO veterinarios (nombre, apellido, especialidad, matricula)
VALUES ('Ana', 'Pérez', 'Clínica', 'VET-123');

INSERT INTO turnos (fecha, hora, motivo, estado, observaciones, mascota_id, veterinario_id)
SELECT DATE '2026-10-01', TIME '10:00:00', 'Control', 'PENDIENTE', 'Control anual', m.id, v.id
FROM mascotas m
JOIN duenos d ON d.id = m.dueno_id
CROSS JOIN veterinarios v
WHERE m.nombre = 'Milo' AND d.dni = '28543210' AND v.matricula = 'VET-123';
