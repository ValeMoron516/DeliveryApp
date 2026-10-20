Módulo: Tarifas Calculadas (CRUD)
1. Crear nueva tarifa
URL: /api/v1/tarifas

Método HTTP: POST

Descripción: Registra una nueva parametrización de costos y límites de peso/volumen para un tipo de vehículo.

Request Body:
{
  "tipoVehiculo": "MOTO",
  "pesoMaxKg": 20.00,
  "largoMaxCm": 50.00,
  "anchoMaxCm": 50.00,
  "altoMaxCm": 40.00,
  "precioBaseVehiculo": 1500.00,
  "precioPorPesoKg": 120.00,
  "precioPorVolumenCm3": 0.0500
}
Respuestas:

Código: 201 (Creado exitosamente)

Response Body:

{
  "id": 1,
  "tipoVehiculo": "MOTO",
  "pesoMaxKg": 20.00,
  "largoMaxCm": 50.00,
  "anchoMaxCm": 50.00,
  "altoMaxCm": 40.00,
  "precioBaseVehiculo": 1500.00,
  "precioPorPesoKg": 120.00,
  "precioPorVolumenCm3": 0.0500
}
Código: 400 Bad Request (Valores negativos o nulos)

Response Body:

{
  "timestamp": "2026-08-17T21:52:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Los precios y dimensiones máximas deben ser mayores a cero."
}
##################################################

2. Obtener todas las tarifas
URL: /api/v1/tarifas

Método HTTP: GET

Descripción: Devuelve la lista completa de configuraciones de tarifas registradas en la plataforma.

Respuestas:

Código: 200 OK (Éxito)

Response Body:

[
  {
    "id": 1,
    "tipoVehiculo": "MOTO",
    "pesoMaxKg": 20.00,
    "largoMaxCm": 50.00,
    "anchoMaxCm": 50.00,
    "altoMaxCm": 40.00,
    "precioBaseVehiculo": 1500.00,
    "precioPorPesoKg": 120.00,
    "precioPorVolumenCm3": 0.0500
  },
  {
    "id": 2,
    "tipoVehiculo": "AUTO",
    "pesoMaxKg": 150.00,
    "largoMaxCm": 120.00,
    "anchoMaxCm": 100.00,
    "altoMaxCm": 80.00,
    "precioBaseVehiculo": 3500.00,
    "precioPorPesoKg": 250.00,
    "precioPorVolumenCm3": 0.0800
  }
]
##################################################

3. Obtener tarifa por ID
URL: /api/v1/tarifas/{id}

Método HTTP: GET

Descripción: Devuelve la información detallada de un esquema de tarifa específico.

Respuestas:

Código: 200 OK (Éxito)

Response Body:

{
  "id": 1,
  "tipoVehiculo": "MOTO",
  "pesoMaxKg": 20.00,
  "largoMaxCm": 50.00,
  "anchoMaxCm": 50.00,
  "altoMaxCm": 40.00,
  "precioBaseVehiculo": 1500.00,
  "precioPorPesoKg": 120.00,
  "precioPorVolumenCm3": 0.0500
}
Código: 404 Not Found (La tarifa no existe)

Response Body:

{
  "timestamp": "2026-08-17T21:52:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró la tarifa calculada con ID: 1"
}
##################################################

4. Actualizar tarifa existente
URL: /api/v1/tarifas/{id}

Método HTTP: PUT

Descripción: Actualiza los montos o límites de dimensiones de una tarifa ya registrada.

Request Body:

{
  "tipoVehiculo": "MOTO",
  "pesoMaxKg": 25.00,
  "largoMaxCm": 50.00,
  "anchoMaxCm": 50.00,
  "altoMaxCm": 40.00,
  "precioBaseVehiculo": 1800.00,
  "precioPorPesoKg": 140.00,
  "precioPorVolumenCm3": 0.0600
}
Respuestas:

Código: 200 OK (Actualizado exitosamente)

Response Body:

{
  "id": 1,
  "tipoVehiculo": "MOTO",
  "pesoMaxKg": 25.00,
  "largoMaxCm": 50.00,
  "anchoMaxCm": 50.00,
  "altoMaxCm": 40.00,
  "precioBaseVehiculo": 1800.00,
  "precioPorPesoKg": 140.00,
  "precioPorVolumenCm3": 0.0600
}
Código: 404 Not Found (La tarifa no existe)

Response Body:

{
  "timestamp": "2026-08-17T21:52:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se puede actualizar. La tarifa con ID 1 no existe."
}
##################################################

5. Eliminar tarifa
URL: /api/v1/tarifas/{id}

Método HTTP: DELETE

Descripción: Elimina una tarifa de la base de datos.

Respuestas:

Código: 204 No Content (Eliminado exitosamente)

Código: 409 Conflict (Restricción por clave foránea RESTRICT)

Response Body:

{
  "timestamp": "2026-08-17T21:52:00",
  "status": 409,
  "error": "Conflict",
  "message": "No se puede eliminar la tarifa porque está asociada a envíos existentes en el sistema."
}
Código: 404 Not Found (La tarifa no existe)

Response Body:

{
  "timestamp": "2026-08-17T21:52:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró la tarifa con ID: 1 para eliminar."
}