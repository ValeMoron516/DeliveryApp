Obtener lista de historiales de ganancia
 * URL: /api/v1/historial-ganancias
 * Método HTTP: GET
 * Descripción: Obtiene una lista paginada de todos los historiales de ganancia registrados en el sistema.
 * Parámetros de consulta (Query Params):
   * page (entero, opcional): Número de página a consultar (por defecto 1).
   * limit (entero, opcional): Cantidad de registros a mostrar por página (por defecto 20).
   * id_repartidor (entero, opcional): Filtra las ganancias pertenecientes a un repartidor específico.
   
Ejemplo de Petición Completa
GET /api/v1/historial-ganancias?page=1&limit=20&id_repartidor=5

Respuestas
 * Código: 200 OK (Éxito con resultados)
 * Response Body:
   {
  "data": [
    {
      "idGanancia": 1,
      "idRepartidor": 5,
      "envioId": 102,
      "montoLiquidado": 1500.50,
      "fechaLiquidacion": "2026-06-06T15:30:00"
    }
  ],
  "meta": {
    "totalItems": 1,
    "itemCount": 1,
    "itemsPerPage": 20,
    "totalPages": 1,
    "currentPage": 1
  }
}

 * Código: 200 OK (Éxito sin resultados)
 * Response Body:
   {
  "data": [],
  "meta": {
    "totalItems": 0,
    "itemCount": 0,
    "itemsPerPage": 20,
    "totalPages": 0,
    "currentPage": 1
  }
}

##################################################

Obtener historial de ganancia por ID
 * URL: /api/v1/historial-ganancias/{id}
 * Método HTTP: GET
 * Descripción: Obtiene el registro de ganancia por su identificador único (id_ganancia).
Respuestas
 * Código: 200 OK (Éxito)
 * Response Body:
   {
    "idGanancia": 1,
    "idRepartidor": 5,
    "envioId": 102,
    "montoLiquidado": 1500.50,
    "fechaLiquidacion": "2026-06-06T15:30:00"
}

 * Código: 404 Not Found (El historial de ganancia con el ID provisto no existe)
 
##################################################

Crear nuevo historial de ganancia
 * URL: /api/v1/historial-ganancias
 * Método HTTP: POST
 * Descripción: Registra un nuevo historial de ganancia asociado a un repartidor y un envío.
 * Request Body:
   {
    "idRepartidor": 5,
    "envioId": 102,
    "montoLiquidado": 1500.50
}

Respuestas
 * Código: 201 Created (Creado exitosamente)
 * Response Body:
   {
    "idGanancia": 1,
    "idRepartidor": 5,
    "envioId": 102,
    "montoLiquidado": 1500.50,
    "fechaLiquidacion": "2026-06-06T15:30:00"
}

 * Código: 400 Bad Request (Datos enviados inválidos o mal formados)
 * Código: 409 Conflict (El envío ya tiene una ganancia liquidada asociada, debido a la restricción única)
 
##################################################

Actualizar historial de ganancia
 * URL: /api/v1/historial-ganancias/{id}
 * Método HTTP: PATCH
 * Descripción: Actualiza los datos de un registro de ganancia en el sistema.
 * Request Body:
   {
    "montoLiquidado": 1750.00
}

Respuestas
 * Código: 200 OK (Actualizado exitosamente)
 * Response Body:
   {
  "idGanancia": 1,
  "idRepartidor": 5,
  "envioId": 102,
  "montoLiquidado": 1750.00,
  "fechaLiquidacion": "2026-06-06T15:30:00"
}

 * Código: 404 Not Found (El historial de ganancia con el ID provisto no existe)
 * Código: 409 Conflict (El envío asociado ya está vinculado a otra ganancia o hay un conflicto de datos)
 
##################################################

Borrar historial de ganancia
 * URL: /api/v1/historial-ganancias/{id}
 * Método HTTP: DELETE
 * Descripción: Elimina un registro de historial de ganancia del sistema.
Respuestas
 * Código: 204 No Content (Borrado exitosamente)
 * Código: 404 Not Found (El historial de ganancia con el ID provisto no existe)
 * Código: 409 Conflict (No se puede eliminar debido a restricciones de integridad con la base de datos)
