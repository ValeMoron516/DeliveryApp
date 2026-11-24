-- SOLO para entorno de desarrollo/pruebas primero. NO aplicar sin backup ni acuerdo del equipo.
-- Columnas nuevas para persistir la constancia declarativa de entrega.
-- Postgres; no borra ni cambia registros existentes.
ALTER TABLE envios ADD COLUMN IF NOT EXISTS receptor_nombre VARCHAR(120);
ALTER TABLE envios ADD COLUMN IF NOT EXISTS fecha_confirmacion TIMESTAMP;
ALTER TABLE envios ADD COLUMN IF NOT EXISTS observaciones_entrega VARCHAR(255);

-- Registros anteriores no se inventan ni se completan automaticamente.
-- La API de seguimiento indicara historialIncompleto=true cuando corresponda.
