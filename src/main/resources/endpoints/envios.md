1. Cotizar y crear solicitud de envío
URL: /api/v1/envios

Método HTTP: POST

Descripción: Registra una nueva orden de envío en el sistema. El backend calcula automáticamente el costoEnvio cruzando los datos del paquete con la tarifa indicada en tarifaCalculadaId. Nace con estado 'BUSCANDO_REPARTIDOR'.

Request Body:

{
  "clienteId": 10,
  "direccionOrigenId": 3,
  "direccionDestinoId": 4,
  "tarifaCalculadaId": 1,
  "pesoPaqueteKg": 4.50,
  "largoPaqueteCm": 30.00,
  "anchoPaqueteCm": 20.00,
  "altoPaqueteCm": 15.00,
  "descripcionPaquete": "Caja de herramientas de precisión",
  "pedidoExternoId": null
}
Respuestas:

Código: 201 (Creado exitosamente)

Response Body:
{
  "idEnvio": 102,
  "clienteId": 10,
  "repartidorId": null,
  "direccionOrigenId": 3,
  "direccionDestinoId": 4,
  "tarifaCalculadaId": 1,
  "fechaSolicitud": "2026-07-26T02:20:00",
  "fechaEntrega": null,
  "estado": "BUSCANDO_REPARTIDOR",
  "pesoPaqueteKg": 4.50,
  "largoPaqueteCm": 30.00,
  "anchoPaqueteCm": 20.00,
  "altoPaqueteCm": 15.00,
  "costoEnvio": 2250.00,
  "descripcionPaquete": "Caja de herramientas de precisión"
}
Código: 400 Bad Request (Las dimensiones superan los límites de la tarifa)

Response Body:

{
  "timestamp": "2026-07-26T02:20:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El paquete supera el peso o volumen máximo permitido para la tarifa del vehículo seleccionado."
}
##################################################

2. Obtener detalles de un envío específico
URL: /api/v1/envios/{id}

Método HTTP: GET

Descripción: Devuelve la información completa y detallada de un envío en base a su ID.

Respuestas:

Código: 200 OK (Éxito)

Response Body:

{
  "idEnvio": 102,
  "clienteId": 10,
  "repartidorId": 5,
  "direccionOrigenId": 3,
  "direccionDestinoId": 4,
  "tarifaCalculadaId": 1,
  "fechaSolicitud": "2026-07-26T01:15:00",
  "fechaEntrega": "2026-07-26T01:45:00",
  "estado": "ENTREGADO",
  "pesoPaqueteKg": 4.50,
  "costoEnvio": 2250.00,
  "descripcionPaquete": "Caja de herramientas de precisión"
}
Código: 404 Not Found (El envío no existe)

Response Body:

{
  "timestamp": "2026-07-26T02:20:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró ningún envío registrado con el ID: 102"
}
##################################################

3. Modificar datos del envío (Antes de ser asignado)
URL: /api/v1/envios/{id}

Método HTTP: PUT

Descripción: Permite corregir o actualizar datos del paquete o la descripción del envío, siempre y cuando todavía no haya sido tomado por un repartidor.

Request Body:

{
  "descripcionPaquete": "Caja de herramientas de precisión (Actualizado: Frágil)",
  "pesoPaqueteKg": 5.00
}
Respuestas:

Código: 200 OK (Actualización exitosa)

Response Body:

{
  "idEnvio": 102,
  "clienteId": 10,
  "repartidorId": null,
  "estado": "BUSCANDO_REPARTIDOR",
  "pesoPaqueteKg": 5.00,
  "costoEnvio": 2400.00,
  "descripcionPaquete": "Caja de herramientas de precisión (Actualizado: Frágil)"
}
Código: 409 Conflict (El envío ya está en viaje y no se puede modificar)

Response Body:

{
  "timestamp": "2026-07-26T02:20:00",
  "status": 409,
  "error": "Conflict",
  "message": "No se pueden modificar los datos del paquete porque el envío ya se encuentra asignado o en viaje."
}
##################################################

4. Cancelar un envío (Baja lógica)
URL: /api/v1/envios/{id}

Método HTTP: DELETE

Descripción: Aplica una baja lógica al envío cambiando su estado a 'CANCELADO'. No borra el registro de la base de datos para mantener la auditoría y el historial del cliente.

Respuestas:

Código: 200 OK (Cancelación exitosa)

Response Body:

{
  "idEnvio": 102,
  "estado": "CANCELADO",
  "message": "El envío ha sido cancelado correctamente."
}
##################################################

5. Asignar un Repartidor al envío
URL: /api/v1/envios/{id}/asignar

Método HTTP: PATCH

Descripción: Vincula un repartidor al viaje. El estado de la orden transiciona de 'BUSCANDO_REPARTIDOR' a 'REPARTIDOR_ASIGNADO'.

Request Body:

{
  "repartidorId": 5
}
Respuestas:

Código: 200 OK (Asignación exitosa)

Response Body:

{
  "idEnvio": 102,
  "repartidorId": 5,
  "estado": "REPARTIDOR_ASIGNADO",
  "message": "Repartidor asignado con éxito al envío."
}
##################################################

6. Actualizar el estado del flujo de envio
URL: /api/v1/envios/{id}/estado

Método HTTP: PATCH

Descripción: Permite al repartidor cambiar los estados del flujo del envío ('EN_ORIGEN', 'EN_CAMINO', 'ENTREGADO'). Al pasar a 'ENTREGADO', el sistema inyecta automáticamente la fechaEntrega actual.

Request Body:
{
  "estado": "ENTREGADO"
}
Respuestas:

Código: 200 OK (Estado modificado)

Response Body:
{
  "idEnvio": 102,
  "estado": "ENTREGADO",
  "fechaEntrega": "2026-07-26T02:20:00",
  "message": "El estado del envío se actualizó a ENTREGADO."
}