# Diseño de endpoints con Markdown



##################################################

## Obtener todas las calificaciones del delivery
* **URL:** `/api/v1/calificacion-delivery`
* **Método HTTP:** `GET`
* **Descripción:** Obtiene una lista de calificaciones registradas para los repartidores.

#### Respuestas
* **Código:** `200` (Éxito)
* **Response Body:**
```json
{
  "data": [
    {
      "id_calificacion": 1,
      "id_repartidor": 3,
      "envio_id": 15,
      "estrellas": 5,
      "comentario_anonimo": "Llegó rápido y fue muy amable."
    }
  ]
}
##################################################
Obtener calificación del delivery por ID
    • URL: /api/v1/calificacion-delivery/{id}
    • Método HTTP: GET
    • Descripción: Obtiene una calificación específica por su identificador.
Respuestas
    • Código: 200 OK (Éxito)
    • Response Body:
{
  "id_calificacion": 1,
  "id_repartidor": 3,
  "envio_id": 15,
  "estrellas": 5,
  "comentario_anonimo": "Llegó rápido y fue muy amable."
}
    • Código: 404 Not Found (La calificación con el ID provisto no existe)
##################################################
Crear calificación del delivery
    • URL: /api/v1/calificacion-delivery
    • Método HTTP: POST
    • Descripción: Registra una nueva calificación para un envío realizado.
    • Request Body:
{
  "id_repartidor": 3,
  "envio_id": 15,
  "estrellas": 5,
  "comentario_anonimo": "Llegó rápido y fue muy amable."
}
Respuestas
    • Código: 201 (Creado exitosamente)
    • Response Body:
{
  "id_calificacion": 1,
  "id_repartidor": 3,
  "envio_id": 15,
  "estrellas": 5,
  "comentario_anonimo": "Llegó rápido y fue muy amable."
}
    • Código: 400 Bad Request (Datos enviados inválidos o mal formados)
    • Código: 409 Conflict (El envío ya posee una calificación registrada)
##################################################
Actualizar calificación del delivery
    • URL: /api/v1/calificacion-delivery/{id}
    • Método HTTP: PATCH
    • Descripción: Actualiza la calificación registrada de un envío.
    • Request Body:
{
  "estrellas": 4,
  "comentario_anonimo": "Buen servicio, pero tardó un poco."
}
Respuestas
    • Código: 200 OK (Actualizado exitosamente)
    • Response Body:
{
  "id_calificacion": 1,
  "id_repartidor": 3,
  "envio_id": 15,
  "estrellas": 4,
  "comentario_anonimo": "Buen servicio, pero tardó un poco."
}
    • Código: 400 Bad Request (Datos enviados inválidos o mal formados)
    • Código: 404 Not Found (La calificación con el ID provisto no existe)
##################################################
Borrar calificación del delivery
    • URL: /api/v1/calificacion-delivery/{id}
    • Método HTTP: DELETE
    • Descripción: Elimina una calificación registrada del sistema.
Respuestas
    • Código: 204 No Content (Borrado exitosamente)
    • Código: 404 Not Found (La calificación con el ID provisto no existe)