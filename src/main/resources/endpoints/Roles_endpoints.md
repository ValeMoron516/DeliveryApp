Obtener lista de roles
 * URL: /api/v1/roles
 * Método HTTP: GET
 * Descripción: Obtiene una lista paginada de todos los roles disponibles en el sistema.


Respuestas
 * Código: 200 OK (Éxito con resultados)
 * Response Body:
   {
  "data": [
    {
      "id": 1,
      "nombre": "ADMIN"
    },
    {
      "id": 2,
      "nombre": "REPARTIDOR"
    }
  ]
}

 * Código: 200 OK (Éxito sin resultados)
 * Response Body:
   {
  "data": []
  }
}

##################################################

Obtener rol por ID
 * URL: /api/v1/roles/{id}
 * Método HTTP: GET
 * Descripción: Obtiene el rol por ID disponible en el sistema.
Respuestas
 * Código: 200 OK (Éxito)
 * Response Body:
   {
    "id": 1,
    "nombre": "ADMIN"
}

 * Código: 404 Not Found (El rol con el ID provisto no existe)
 
##################################################

Crear nuevo rol
 * URL: /api/v1/roles
 * Método HTTP: POST
 * Descripción: Crea un nuevo rol en el sistema.
 * Request Body:
   {
    "id": 3,
    "nombre": "CLIENTE"
}

Respuestas
 * Código: 201 Created (Creado exitosamente)
 * Response Body:
   {
    "id": 3,
    "nombre": "CLIENTE"
}

 * Código: 400 Bad Request (Datos enviados inválidos o mal formados)
 * Código: 409 Conflict (El nombre ya está siendo utilizado por otro rol)
 
##################################################

Actualizar rol
 * URL: /api/v1/roles/{id}
 * Método HTTP: PATCH
 * Descripción: Actualiza el rol en el sistema.
 * Request Body:
   {
    "nombre": "SUPER_ADMIN"
}

Respuestas
 * Código: 200 OK (Actualizado exitosamente)
 * Response Body:
   {
  "id": 1,
  "nombre": "SUPER_ADMIN"
}

 * Código: 404 Not Found (El rol con el ID provisto no existe)
 * Código: 409 Conflict (El nombre ya está siendo utilizado por otro rol)
 
##################################################

Borrar rol
 * URL: /api/v1/roles/{id}
 * Método HTTP: DELETE
 * Descripción: Elimina un rol del sistema.
Respuestas

 * Código: 204 No Content (Borrado exitosamente)
 * Código: 404 Not Found (El rol con el ID provisto no existe)
 * Código: 409 Conflict (No se puede eliminar; el rol tiene usuarios asociados)
