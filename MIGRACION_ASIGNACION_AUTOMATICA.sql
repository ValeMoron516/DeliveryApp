-- APLICAR EN ENTORNO DE PRUEBAS TRAS RESPALDO Y APROBACION DEL EQUIPO.
-- Preserva envios anteriores como asignacion_automatica=false.
-- No borra ni altera los estados ni tarifas existentes.
ALTER TABLE public.envios
    ADD COLUMN IF NOT EXISTS asignacion_automatica BOOLEAN NOT NULL DEFAULT FALSE;

-- Clave generada por frontend; evita registrar la misma solicitud dos veces.
ALTER TABLE public.envios
    ADD COLUMN IF NOT EXISTS clave_solicitud VARCHAR(64);
CREATE UNIQUE INDEX IF NOT EXISTS uq_envios_clave_solicitud
    ON public.envios(clave_solicitud)
    WHERE clave_solicitud IS NOT NULL;
