METODO: REPARTIDORES (CRUD)

1. Registrar nuevo repartidor
URL: /api/v1/repartidores
Metodo HTML: POST
Descripción: Activa el perfil de repartidor para un usuario ya registrado en el sistema. Vincula
el id del usuario con el idRepartidor.

Request Body:
{
  "idRepartidor": 5,
  "fotoUrl": "https://cdn.DeliveryApp.com/fotos/repartidor_5.jpg",
  "vehiculoActId": 1
}
Respuestas:
Codigo 201 (Creado exitosamente):
Response Body:
{
  "idRepartidor": 5,
  "fotoUrl": "https://cdn.DeliveryApp.com/fotos/repartidor_5.jpg",
  "disponible": 0,
  "saldoAcumulado": 0.00,
  "calificacionPromedio": 0.00,
  "tasaRechazoInterna": 0,
  "vehiculoActId": 1
}

Codigo 400 Bad Request (El usuario no existe o ya es un repartidor)
Response Body:
{
  "timestamp": "2026-07-14T19:10:33",
  "status": 400,
  "error": "Bad Request",
  "message": "No se puede crear el perfil: El usuario con ID 5 no existe o ya tiene un perfil de repartidor activo."
}

#########################################################################

2.Obtener perfil de un repartidor
URL: /api/v1/repartidores/{id}
Metodo HTML: GET
Descripción: Obtiene los datos del perfil de un repartidor especifico mediante la busqueda de su id.
Respuestas:
Codigo 200 OK (Exito)
Response Body:
{
  "idRepartidor": 5,
  "fotoUrl": "https://cdn.DeliveryApp.com/fotos/repartidor_5.jpg",
  "disponible": 1,
  "saldoAcumulado": 14500.50,
  "calificacionPromedio": 4.85,
  "tasaRechazoInterna": 2,
  "vehiculoActId": 1
}
Codigo 404 Not Found (El repartidor no existe en el sistema)
Response Body:
{
  "timestamp": "2026-07-14T19:10:33",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró un perfil de repartidor asociado al ID: 5"
}

#########################################################################
3. Actualizar datos de un repartidor
URL: /api/v1/repartidores/{id}
Metodo HTML: PUT
Descripción: Modificar los datos del perfil de un repartidor registrado mediante la busqueda de su id.
Request Body:
{
  "fotoUrl": "https://cdn.DeliveryApp.com/fotos/repartidor_5_actualizada.jpg",
  "disponible": 1,
  "vehiculoActId": 2
}

Respuestas:
Código 200 OK (Actualización exitosa)

Response Body:
{
  "idRepartidor": 5,
  "fotoUrl": "https://cdn.DeliveryApp.com/fotos/repartidor_5_actualizada.jpg",
  "disponible": 1,
  "saldoAcumulado": 14500.50,
  "calificacionPromedio": 4.85,
  "tasaRechazoInterna": 2,
  "vehiculoActId": 2
}
Código 404 Not Found (El repartidor no existe)

Response Body:
{
  "timestamp": "2026-07-14T19:10:33",
  "status": 404,
  "error": "Not Found",
  "message": "No se puede actualizar. El repartidor con ID: 5 no existe."
}
########################################################################################

4. Eliminar perfil de repartidor
URL: /api/repartidores/{id}
Metodo HTML: DELETE
Descripcion: Eliminar el perfil de un repartidor del sistema mediante su id. Debido a la regla DELETE ON CASCADE, se eliminaran los registros de vehiculos asociados al repartidos

Respuestas:
Codigo 200 OK (Eliminado exitosamente)

Response Body:
{
  "idRepartidor": 5,
  "status": "DELETED",
  "message": "El perfil de repartidor y sus asociaciones de vehículos fueron eliminados correctamente."
}

Codigo 404 Not Found (El repartidor no existe en el sistema)

Response Body:
{
  "timestamp": "2026-07-14T19:10:33",
  "status": 404,
  "error": "Not Found",
  "message": "No se registra ningun repartidor con ese ID dentro del sistema. "
}

5.Cambiar estado de disponibilidad (Conectarse / Desconectarse)
URL: /api/v1/repartidores/{id}/disponibilidad
Método HTTP: PATCH
Descripción: Permite al repartidor ponerse "en línea" para recibir pedidos (disponible: 1) o ponerse "fuera de servicio" (disponible: 0) cuando termina su turno.

Request Body:

{
  "disponible": 1
}
Respuestas:

Código: 200 OK (Estado actualizado)

Response Body:
{
  "idRepartidor": 5,
  "disponible": 1,
  "message": "El repartidor ahora se encuentra ACTIVO y disponible para recibir pedidos."
}
Código: 400 Bad Request (No tiene vehículo activo asignado)

Response Body:
{
  "timestamp": "2026-07-14T20:50:56",
  "status": 400,
  "error": "Bad Request",
  "message": "No podés ponerte en línea sin antes haber seleccionado un vehículo de trabajo activo."
}
##################################################
6. Cambiar de vehiculo activo para la jornada
URL /api/v1/vehiculos-activos/{id}/repartidores
Metodo HTTP: PATCH
Descripcion: Permite al repartidor elegir cual de los vehiculos que tiene registrados va a usar para trabajar hoy.

Request Body:
{
    "vehiculoActId": 2
}
Respuestas:
Codigo 200 OK (vehiculo activo cambiado)

Response Body:
{
    "idRepartidor": 5,
    "vehiculoActId": 2,
    "message": "Vehículo de trabajo actualizado correctamente para la jornada actual."
}
Código: 400 Bad Request (El vehículo no le pertenece)

Response Body:

{
  "timestamp": "2026-07-14T20:50:56",
  "status": 400,
  "error": "Bad Request",
  "message": "El vehículo seleccionado no está asociado al perfil de este repartidor."
}
##################################################

7. Consultar historial de envíos realizados
URL: /api/v1/envios/{id}/repartidores
Método HTTP: GET
Descripción: Devuelve la lista de todos los envíos que este repartidor ya entregó o tiene asignados actualmente. Soporta un parámetro opcional en la URL para filtrar por estado (estado=ENTREGADO o estado=REPARTIDOR_ASIGNADO).

Respuestas:
Código: 200 OK (Éxito)

Response Body:

[
  {
    "idEnvio": 102,
    "fechaSolicitud": "2026-07-14T19:07:19",
    "fechaEntrega": "2026-07-14T19:45:00",
    "estado": "ENTREGADO",
    "costoEnvio": 2250.00,
    "direccionOrigen": "Av. Mitre 450",
    "direccionDestino": "Calle San Martín 1230"
  },
  {
    "idEnvio": 105,
    "fechaSolicitud": "2026-07-14T20:15:00",
    "fechaEntrega": null,
    "estado": "REPARTIDOR_ASIGNADO",
    "costoEnvio": 1800.00,
    "direccionOrigen": "Av. Belgrano 800",
    "direccionDestino": "Pellegrini 340"
  }
]
##################################################

8. Consultar saldo y liquidación de ganancias
URL: /api/v1/repartidores/{id}/saldo
Método HTTP: GET
Descripción: Permite al repartidor ver cuánto dinero acumulado tiene para cobrar por sus entregas realizadas y su tasa de rendimiento actual.

Respuestas:
Código: 200 OK (Éxito)

Response Body:

{
  "idRepartidor": 5,
  "saldoAcumulado": 14500.50,
  "cantidadEnviosEntregados": 12,
  "calificacionPromedio": 4.85,
  "tasaRechazoInterna": 2
}
