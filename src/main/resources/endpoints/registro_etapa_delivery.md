### Obtener lista de registros de etapas de delivery
*   **URL:** `/api/v1/registro-etapas-delivery`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene una lista paginada de todos los registros de etapas asociados a los envíos del sistema.
*   **Parámetros de consulta:**
  * `page` (entero, opcional): Número de página a consultar (por defecto `1`).
  * `limit` (entero, opcional): Cantidad de registros por página (por defecto `20`).

#### Ejemplo de Petición Completa
```http
GET /api/v1/registro-etapas-delivery?page=1&limit=20
```
#### Respuestas
*   **Código:** `200 OK` (Éxito con resultados)
*   **Response Body:**
```json
{
  "data": [
    {
      "idRegistro": 1,
      "envioId": 15,
      "etapaAlcanzada": "REPARTIDOR_ASIGNADO",
      "fechaHora": "2026-07-14T09:15:00",
      "latitud": -34.603722,
      "longitud": -58.381592,
      "fueValido": true
    },
    {
      "idRegistro": 2,
      "envioId": 15,
      "etapaAlcanzada": "EN_CAMINO",
      "fechaHora": "2026-07-14T09:35:00",
      "latitud": -34.605122,
      "longitud": -58.379431,
      "fueValido": true
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

### Obtener registro de etapa por ID
*   **URL:** `/api/v1/registro-etapas-delivery/{id}`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene la información de un registro de etapa de delivery mediante su identificador.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador único del registro.

#### Ejemplo de Petición Completa
```http
GET /api/v1/registro-etapas-delivery/1
```
#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "idRegistro": 1,
  "envioId": 15,
  "etapaAlcanzada": "REPARTIDOR_ASIGNADO",
  "fechaHora": "2026-07-14T09:15:00",
  "latitud": -34.603722,
  "longitud": -58.381592,
  "fueValido": true
}
```
*   **Código:** `404 Not Found` (El registro solicitado no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Crear un registro de etapa de delivery
*   **URL:** `/api/v1/registro-etapas-delivery`
*   **Método HTTP:** `POST`
*   **Descripción:** Registra una nueva etapa alcanzada durante el proceso de entrega de un envío.
*   **Request Body:**
```json
{
  "envioId": 15,
  "etapaAlcanzada": "ENTREGADO",
  "latitud": -34.603722,
  "longitud": -58.381592,
  "fueValido": true
}
```
#### Respuestas
*   **Código:** `201 Created`
*   **Response Body:**
```json
{
  "idRegistro": 8,
  "envioId": 15,
  "etapaAlcanzada": "ENTREGADO",
  "fechaHora": "2026-07-14T10:25:00",
  "latitud": -34.603722,
  "longitud": -58.381592,
  "fueValido": true
}
```
*   **Código:** `400 Bad Request` (Datos enviados inválidos o incompletos.)
*   **Código:** `404 Not Found` (El envío especificado no existe.)
*   **Código:** `409 Conflict` (La etapa ya fue registrada para ese envío.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Actualizar un registro de etapa de delivery
*   **URL:** `/api/v1/registro-etapas-delivery/{id}`
*   **Método HTTP:** `PATCH`
*   **Descripción:** Actualiza la información de un registro de etapa existente.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del registro.
*   **Request Body:**
```json
{
  "latitud": -34.603900,
  "longitud": -58.381700,
  "fueValido": false
}
```
#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "idRegistro": 8,
  "envioId": 15,
  "etapaAlcanzada": "ENTREGADO",
  "fechaHora": "2026-07-14T10:25:00",
  "latitud": -34.603900,
  "longitud": -58.381700,
  "fueValido": false
}
```
*   **Código:** `400 Bad Request` (Datos enviados inválidos.)
*   **Código:** `404 Not Found` (El registro solicitado no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Eliminar un registro de etapa de delivery
*   **URL:** `/api/v1/registro-etapas-delivery/{id}`
*   **Método HTTP:** `DELETE`
*   **Descripción:** Elimina un registro de etapa de delivery del sistema.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del registro.
#### Ejemplo de Petición Completa
```http
DELETE /api/v1/registro-etapas-delivery/8
```
#### Respuestas
*   **Código:** `204 No Content` (El registro fue eliminado correctamente.)
*   **Código:** `404 Not Found` (El registro solicitado no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`

##################################################

### Obtener historial de etapas de un envío
*   **URL:** `/api/v1/envios/{id}/registro-etapas`
*   **Método HTTP:** `GET`
*   **Descripción:** Obtiene el historial completo de etapas registradas para un envío específico, ordenadas cronológicamente.
*   **Parámetros de la URL:**
  * `id` (Long): Identificador del envío.
#### Ejemplo de Petición Completa
```http
GET /api/v1/envios/15/registro-etapas
```
#### Respuestas
*   **Código:** `200 OK`
*   **Response Body:**
```json
{
  "data": [
    {
      "idRegistro": 1,
      "etapaAlcanzada": "REPARTIDOR_ASIGNADO",
      "fechaHora": "2026-07-14T09:15:00"
    },
    {
      "idRegistro": 2,
      "etapaAlcanzada": "EN_CAMINO",
      "fechaHora": "2026-07-14T09:35:00"
    },
    {
      "idRegistro": 3,
      "etapaAlcanzada": "ENTREGADO",
      "fechaHora": "2026-07-14T10:25:00"
    }
  ]
}
```
*   **Código:** `200 OK` (El envío no posee registros de seguimiento.)
```json
{
  "data": []
}
```
*   **Código:** `404 Not Found` (El envío especificado no existe.)
*   **Código:** `500 Internal Server Error`
*   **Código:** `503 Service Unavailable`