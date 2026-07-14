### Obtener lista de vehículos
*   **URL:** `/api/v1/vehiculos`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene una lista paginada de todos los vehículos registrados en el sistema.
*   **Parámetros de consulta:**
  * `page` (entero, opcional): Número de página a consultar (por defecto `1`).
  * `limit` (entero, opcional): Cantidad de registros por página (por defecto `20`).

#### Ejemplo de Petición Completa
```http
GET /api/v1/vehiculos?page=1&limit=20
```
#### Respuestas
*   **Código:** `200 OK` (Éxito con resultados)
*   **Response Body:**
```json
{
  "data": [
    {
      "idVehiculo": 1,
      "tipo": "Motocicleta",
      "descripcion": "Motocicleta 150cc"
    },
    {
      "idVehiculo": 2,
      "tipo": "Automóvil",
      "descripcion": "Automóvil utilitario"
    }
  ],
  "meta": {
    "totalItems": 2,
    "itemCount": 2,
    "itemsPerPage": 20,
    "totalPages": 1,
    "currentPage": 1
  }
}
```
*   **Código:** `200 OK` (Éxito sin resultados)
```json
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
```
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Obtener vehículo por ID
*   **URL:** `/api/v1/vehiculos/{id}`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene la información de un vehículo registrado mediante su identificador.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador único del vehículo.

#### Ejemplo de Petición Completa
```http
GET /api/v1/vehiculos/1
```
#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "idVehiculo": 1,
  "tipo": "Motocicleta",
  "descripcion": "Motocicleta 150cc"
}
```
*   **Código:** `404 Not Found` (El vehículo no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Crear un vehículo
*   **URL:** `/api/v1/vehiculos`
*   **Método HTTP:** `POST`
*   **Descripción:** Registra un nuevo tipo de vehículo disponible para los repartidores.
*   **Request Body:**
```json
{
  "tipo": "Bicicleta",
  "descripcion": "Bicicleta urbana"
}
```
#### Respuestas
*   **Código:** `201 Created`
*   **Response Body:**
```json
{
  "idVehiculo": 3,
  "tipo": "Bicicleta",
  "descripcion": "Bicicleta urbana"
}
```
*   **Código:** `400 Bad Request` (Datos enviados inválidos o incompletos.)
*   **Código:** `409 Conflict` (Ya existe un vehículo con ese tipo.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Actualizar un vehículo
*   **URL:** `/api/v1/vehiculos/{id}`
*   **Método HTTP:** `PATCH`
*   **Descripción:** Actualiza la información de un vehículo registrado.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del vehículo.
*   **Request Body:**
```json
{
  "descripcion": "Motocicleta 250cc"
}
```

#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "idVehiculo": 1,
  "tipo": "Motocicleta",
  "descripcion": "Motocicleta 250cc"
}
```
*   **Código:** `400 Bad Request` (Datos enviados inválidos o mal formados.)
*   **Código:** `404 Not Found` (El vehículo no existe.)
*   **Código:** `409 Conflict` (El tipo de vehículo ya está registrado.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Eliminar un vehículo
*   **URL:** `/api/v1/vehiculos/{id}`
*   **Método HTTP:** `DELETE`
*   **Descripción:** Elimina un vehículo registrado del sistema.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del vehículo.

#### Ejemplo de Petición Completa
```http
DELETE /api/v1/vehiculos/1
```

#### Respuestas
*   **Código:** `204 No Content` (El vehículo fue eliminado correctamente.)
*   **Código:** `404 Not Found` (El vehículo no existe.)
*   **Código:** `409 Conflict` (No es posible eliminar el vehículo porque está asociado a uno o más repartidores.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Obtener repartidores asociados a un vehículo
*   **URL:** `/api/v1/vehiculos/{id}/repartidores`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene el listado de repartidores que tienen asociado un determinado vehículo.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del vehículo.

#### Ejemplo de Petición Completa
```http
GET /api/v1/vehiculos/1/repartidores
```

#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "data": [
    {
      "idRepartidor": 7,
      "nombre": "Juan Pérez",
      "patenteMatricula": "AF123BC",
      "esPrincipal": true
    },
    {
      "idRepartidor": 15,
      "nombre": "María Gómez",
      "patenteMatricula": "AE456CD",
      "esPrincipal": false
    }
  ]
}
```
*   **Código:** `200 OK` (No hay repartidores asociados.)
```json
{
  "data": []
}
```
*   **Código:** `404 Not Found` (El vehículo no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`