# Módulo: Calificaciones del Delivery (CRUD)

##################################################

## Registrar nueva calificación
* **URL:** `/api/v1/calificaciones`
* **Método HTTP:** `POST`
* **Descripción:** Registra una nueva calificación realizada sobre un repartidor a partir de un envío completado.

* **Request Body:**
```json
{
  "idRepartidor": 3,
  "envioId": 15,
  "estrellas": 5,
  "comentarioAnonimo": "Llegó rápido y fue muy amable."
}