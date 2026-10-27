# DeliveryApp

Backend de una plataforma de logística orientada a repartidores locales. El sistema está diseñado para gestionar perfiles, disponibilidad, asignación de envíos, seguimiento de etapas, ganancias, calificaciones y notificaciones, con integración con una plataforma comercial mediante una API.

## Descripción del proyecto

El objetivo del proyecto es ofrecer una API REST que conecte los comercios adheridos con los repartidores disponibles y permita registrar de forma ordenada el ciclo de cada entrega.

El repartidor puede administrar su perfil y sus vehículos, activar o desactivar su disponibilidad, recibir solicitudes asignadas por los comercios, avanzar por las etapas obligatorias de un envío y consultar sus ganancias y valoraciones. El backend mantiene un historial de los eventos relevantes para facilitar la trazabilidad y la auditoría de las operaciones.

La aplicación está pensada para un entorno local y para una futura integración con una aplicación móvil. La especificación contempla notificaciones por canales como SMS o WhatsApp Business, según las integraciones que se habiliten en el proyecto.

### Objetivos

- Implementar un backend organizado mediante una API REST.
- Administrar usuarios, roles y perfiles de repartidores.
- Gestionar vehículos y el vehículo seleccionado para cada jornada.
- Registrar direcciones de origen y destino de los envíos.
- Calcular costos a partir de parámetros tarifarios configurables.
- Controlar la asignación y el avance de los envíos mediante estados definidos.
- Mantener un historial auditable de las etapas de cada entrega.
- Registrar las ganancias y las calificaciones asociadas a los envíos finalizados.
- Facilitar la comunicación con la plataforma comercial y con los repartidores.

## Funcionalidades principales

### Usuarios, autenticación y roles

- Registro y administración de usuarios.
- Asignación de roles, por ejemplo `CLIENTE`, `REPARTIDOR` y `ADMINISTRADOR`.
- Uso del correo electrónico como identificador de acceso en el modelo compartido descrito en la base de datos.
- Almacenamiento de contraseñas mediante un hash seguro.
- Posibilidad de que una misma cuenta tenga más de un rol.

> La especificación funcional de la aplicación de repartidores también contempla autenticación telefónica mediante OTP. La estructura SQL incluida más abajo todavía no modela el ciclo de vida de esos códigos; debe alinearse con la implementación de autenticación del repositorio.

### Perfil y disponibilidad del repartidor

- Asociación de un perfil de repartidor con un usuario de la plataforma.
- Almacenamiento de foto de perfil, disponibilidad, saldo acumulado y calificación promedio.
- Selección del vehículo con el que trabaja el repartidor.
- Bloqueo de nuevas asignaciones mientras el repartidor tenga un envío activo.
- Restauración de la disponibilidad al completar el envío, siempre que así lo implemente la lógica de negocio.

### Gestión de vehículos

- Catálogo de medios de transporte.
- Asociación de uno o varios vehículos con un repartidor mediante una tabla intermedia.
- Registro opcional de patente o matrícula.
- Identificación del vehículo principal y del vehículo elegido para la jornada.

### Envíos y tarifas

- Registro del cliente, las direcciones de origen y destino y la información del envío.
- Asociación opcional con el repartidor asignado.
- Vinculación con pedidos originados en la plataforma comercial mediante `pedido_externo_id`.
- Registro de distancia, costo, fechas y estado del envío.
- Tabla de parámetros tarifarios por tipo de vehículo, peso y dimensiones del paquete.

### Flujo de entrega

La especificación funcional define una secuencia guiada para evitar que se cierren etapas fuera de orden:

| Etapa | Acción prevista | Resultado esperado |
| --- | --- | --- |
| 1. Asignación | El repartidor recibe una solicitud y decide si la acepta dentro del tiempo establecido. | El envío queda asignado o vuelve a estar disponible para el comercio. |
| 2. Llegada al comercio | El repartidor informa que llegó al punto de origen. | Se registra el evento y se habilita el paso de recogida. |
| 3. Recogida | Se valida el código de recogida definido por el sistema. | Se registra la recogida y el envío pasa a la siguiente etapa. |
| 4. Llegada al cliente | El repartidor informa que llegó al destino. | Se registra el evento y se habilita la validación final. |
| 5. Entrega | Se valida el código de entrega. | El envío queda entregado y se registra la ganancia correspondiente. |

### Seguimiento y auditoría

- Registro cronológico de los eventos importantes de cada envío.
- Almacenamiento de la etapa alcanzada, fecha y resultado de la validación.
- Campos de latitud y longitud previstos para los registros de etapa.
- Historial utilizable para revisar incidencias o disputas sobre una entrega.

El alcance inicial documentado no contempla mapas integrados ni seguimiento GPS continuo.

### Ganancias y calificaciones

- Registro de la liquidación correspondiente a cada envío completado.
- Consulta de ganancias por día, semana y acumulado histórico, si esos filtros están implementados en los endpoints.
- Calificación del repartidor con estrellas y un comentario opcional.
- Asociación de la calificación con un envío específico para evitar reseñas sin una operación real.
- Presentación de comentarios bajo una política de privacidad que evite exponer innecesariamente la identidad de quien califica.

### Notificaciones

El diseño contempla una bandeja de notificaciones y la integración con canales como notificaciones, SMS o WhatsApp Business. La disponibilidad de cada canal depende de las credenciales, los servicios externos y el código de integración configurados en el proyecto.

## Tecnologías utilizadas

| Tecnología | Propósito |
| --- | --- |
| Java | Lenguaje principal del backend. |
| Spring Boot | Desarrollo y configuración de la aplicación. |
| Spring Web | Implementación de endpoints REST, si está incluida como dependencia. |
| Spring Data JPA | Persistencia mediante entidades y repositorios, si está incluida como dependencia. |
| PostgreSQL | Base de datos relacional. |
| Maven | Gestión de dependencias y construcción del proyecto, si se utiliza Maven. |
| Git y GitHub | Control de versiones y alojamiento del código fuente. |

## Arquitectura y organización del backend

Se propone una arquitectura por capas para separar las solicitudes HTTP, las reglas de negocio y el acceso a los datos.

```text
Aplicación móvil / Plataforma comercial
                 |
                 v
             API REST
                 |
                 v
            Controllers
                 |
                 v
             Services
                 |
                 v
           Repositories
                 |
                 v
             PostgreSQL
```

**Responsabilidades de cada capa:**

- **Controllers:** reciben solicitudes HTTP y devuelven las respuestas de la API.
- **Services:** implementan las reglas de negocio, validan las transiciones de estado, coordinan las asignaciones y registran las operaciones financieras.
- **Repositories:** consultan y persisten la información en PostgreSQL.
- **Entities / Models:** representan usuarios, repartidores, vehículos, envíos y sus relaciones.
- **DTOs:** permiten definir los datos de entrada y salida sin exponer entidades completas cuando no corresponde.
- **Integraciones externas:** conectan el backend con la plataforma comercial y con los canales de notificación habilitados.

## Modelo de datos

El esquema SQL propuesto utiliza las siguientes entidades principales:

| Tabla | Descripción |
| --- | --- |
| `usuarios` | Datos personales y credenciales de las cuentas. |
| `roles` | Catálogo de roles disponibles. |
| `usuario_roles` | Relación muchos a muchos entre usuarios y roles. |
| `repartidor` | Información operativa y financiera del repartidor. |
| `vehiculo` | Catálogo de vehículos. |
| `repartidor_vehiculo` | Vehículos asociados a cada repartidor. |
| `direcciones` | Direcciones guardadas por los usuarios. |
| `tarifas_calculadas` | Parámetros que utiliza el backend para calcular tarifas. |
| `envios` | Datos principales de cada envío. |
| `registro_etapa_delivery` | Historial de eventos y etapas de los envíos. |
| `historial_ganancia` | Liquidaciones de las ganancias de los repartidores. |
| `calificacion_delivery` | Calificaciones y comentarios de los clientes. |
| `notificacion_delivery` | Historial de notificaciones. |

### Relaciones entre entidades

- **Usuarios y roles:** muchos a muchos, mediante `usuario_roles`.
- **Usuarios y repartidores:** uno a uno; `repartidor.id_repartidor` referencia a `usuarios.id`.
- **Repartidores y vehículos:** muchos a muchos, mediante `repartidor_vehiculo`.
- **Usuarios y direcciones:** uno a muchos.
- **Usuarios y envíos:** uno a muchos, mediante `envios.cliente_id`.
- **Repartidores y envíos:** uno a muchos; un envío puede no tener repartidor mientras espera una asignación.
- **Direcciones y envíos:** cada envío referencia una dirección de origen y otra de destino.
- **Envíos y registros de etapa:** uno a muchos.
- **Repartidores e historial de ganancias:** uno a muchos.
- **Envíos e historial de ganancias:** uno a uno lógico, reforzado por la restricción `UNIQUE` sobre `historial_ganancia.envio_id`.
- **Repartidores y calificaciones:** uno a muchos.
- **Envíos y calificaciones:** uno a uno lógico, reforzado por la restricción `UNIQUE` sobre `calificacion_delivery.envio_id`.
- **Envíos y notificaciones:** uno a muchos.

## Decisiones de diseño y justificación

### 1. PostgreSQL como base de datos relacional

El sistema relaciona usuarios, repartidores, direcciones, vehículos y envíos. PostgreSQL permite representar estas relaciones mediante claves primarias y foráneas y mantener la coherencia referencial de los datos.

### 2. Roles separados de los usuarios

La tabla `roles` evita repetir la definición de cada rol. La tabla `usuario_roles` permite que una cuenta tenga varios roles y utiliza una clave primaria compuesta para impedir que el mismo rol se asigne dos veces al mismo usuario.

### 3. Perfil de repartidor separado de la cuenta

La tabla `repartidor` extiende la información de `usuarios` con los datos propios de la logística, como disponibilidad, saldo y reputación. Así, los atributos operativos no se mezclan con los datos generales de autenticación.

### 4. Tabla intermedia para vehículos

`repartidor_vehiculo` representa la relación muchos a muchos entre repartidores y vehículos. También permite guardar datos específicos de esa asociación, como la patente y si se trata del vehículo principal.

### 5. Envíos como entidad central

La tabla `envios` concentra los datos esenciales de la operación. Sus claves foráneas apuntan al cliente y a las direcciones, mientras que `pedido_externo_id` permite vincular el envío con un pedido de otro sistema sin duplicar necesariamente toda su estructura de datos.

### 6. Historial de etapas independiente

`registro_etapa_delivery` mantiene múltiples eventos asociados a un envío. Separar el historial de la tabla `envios` evita sobrescribir los pasos anteriores y permite reconstruir la secuencia de una operación.

### 7. Registro contable separado

`historial_ganancia` conserva el importe liquidado y la fecha correspondiente. La restricción de unicidad sobre `envio_id` evita crear más de una liquidación para el mismo envío desde la base de datos.

### 8. Conservación de las calificaciones

`calificacion_delivery` relaciona una valoración con el repartidor y el envío evaluado. La restricción `UNIQUE` sobre `envio_id` limita a una calificación por envío. La aplicación debe validar además que las estrellas estén entre 1 y 5.

### 9. Tarifas configurables

`tarifas_calculadas` separa los parámetros de cotización del resto de las operaciones. Los servicios deben validar qué tarifa corresponde y cómo se calcula el precio final; la fórmula definitiva debe ser consistente con las reglas comerciales adoptadas por el proyecto.

### 10. Estados y reglas de transición

La tabla `envios` almacena un estado general, mientras que `registro_etapa_delivery` conserva el detalle cronológico de los eventos. Los servicios deben validar las transiciones permitidas para impedir que una entrega se cierre saltándose etapas.

### 11. Seguridad de credenciales y tokens

Las contraseñas deben almacenarse usando un algoritmo de hash apropiado para contraseñas, como BCrypt. Los códigos de recogida y entrega deben validarse en el backend, tener una caducidad definida y no exponerse al repartidor cuando correspondan al cliente.

### 12. Políticas de eliminación

Las claves foráneas del esquema utilizan políticas como `ON DELETE CASCADE`, `ON DELETE SET NULL` y `ON DELETE RESTRICT`, según la relación. Estas reglas deben revisarse junto con las necesidades de auditoría: eliminar un registro principal no siempre debería eliminar información financiera o histórica que deba conservarse.

## Requisitos previos

Antes de ejecutar el proyecto, es necesario disponer de:

- **Java JDK:** una versión compatible con la configurada en el proyecto.
- **PostgreSQL:** servidor de base de datos instalado y en funcionamiento.
- **Maven:** si el repositorio no incluye Maven Wrapper (`mvnw` o `mvnw.cmd`).
- **Git:** para clonar el repositorio.
- Un editor o IDE compatible con Java, como IntelliJ IDEA, Eclipse o Visual Studio Code.

## Cómo poner en marcha el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/ValeMoron516/DeliveryApp.git
cd DeliveryApp
```

### 2. Crear la base de datos

Desde PostgreSQL, creá una base de datos para el proyecto:

```sql
CREATE DATABASE deliveryapp;
```

Conectate a `deliveryapp` y ejecutá el script de creación de tablas que aparece en la sección siguiente.

### 3. Crear las tablas

El siguiente esquema reproduce el modelo relacional descrito para el backend:

```sql
-- MODULO 1: USUARIOS, AUTENTICACION Y ROLES

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id INT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuario_roles (
    usuario_id INT,
    rol_id INT,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuarioroles_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarioroles_rol FOREIGN KEY (rol_id)
        REFERENCES roles(id) ON DELETE CASCADE
);

-- MODULO 2: PERFIL DEL REPARTIDOR Y VEHICULOS

CREATE TABLE vehiculo (
    id_vehiculo INT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE repartidor (
    id_repartidor INT PRIMARY KEY,
    foto_url VARCHAR(255),
    disponible INT NOT NULL DEFAULT 0,
    saldo_acumulado DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    calificacion_promedio DECIMAL(3,2) NOT NULL DEFAULT 5.00,
    tasa_rechazo_interna INT NOT NULL DEFAULT 0,
    vehiculo_act_id INT,
    CONSTRAINT fk_repartidor_usuario FOREIGN KEY (id_repartidor)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_repartidor_vehiculo_activo FOREIGN KEY (vehiculo_act_id)
        REFERENCES vehiculo(id_vehiculo) ON DELETE SET NULL
);

CREATE TABLE repartidor_vehiculo (
    id_repartidor INT,
    id_vehiculo INT,
    patente_matricula VARCHAR(50),
    es_principal INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_repartidor, id_vehiculo),
    CONSTRAINT fk_garaje_repartidor FOREIGN KEY (id_repartidor)
        REFERENCES repartidor(id_repartidor) ON DELETE CASCADE,
    CONSTRAINT fk_garaje_vehiculo FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculo(id_vehiculo) ON DELETE CASCADE
);

-- MODULO 3: DIRECCIONES, ENVIOS Y TARIFAS

CREATE TABLE direcciones (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL,
    calle VARCHAR(150) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(50) NOT NULL,
    indicaciones_extra VARCHAR(255),
    CONSTRAINT fk_direcciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE tarifas_calculadas (
    id SERIAL PRIMARY KEY,
    tipo_vehiculo VARCHAR(255),
    peso_max_kg DECIMAL(10, 2),
    largo_max_cm DECIMAL(10, 2),
    ancho_max_cm DECIMAL(10, 2),
    alto_max_cm DECIMAL(10, 2),
    precio_base_vehiculo DECIMAL(10, 2),
    precio_por_peso_kg DECIMAL(10, 2),
    precio_por_volumen_cm3 DECIMAL(10, 4)
);

CREATE TABLE envios (
    id SERIAL PRIMARY KEY,
    cliente_id INT NOT NULL,
    repartidor_id INT,
    direccion_origen_id INT NOT NULL,
    direccion_destino_id INT NOT NULL,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'BUSCANDO_REPARTIDOR',
    distancia_km DECIMAL(5,2) NOT NULL,
    costo_envio DECIMAL(10,2) NOT NULL,
    descripcion_paquete VARCHAR(255),
    pedido_externo_id INT,
    CONSTRAINT fk_envios_cliente FOREIGN KEY (cliente_id)
        REFERENCES usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_envios_repartidor FOREIGN KEY (repartidor_id)
        REFERENCES repartidor(id_repartidor) ON DELETE SET NULL,
    CONSTRAINT fk_envios_origen FOREIGN KEY (direccion_origen_id)
        REFERENCES direcciones(id) ON DELETE RESTRICT,
    CONSTRAINT fk_envios_destino FOREIGN KEY (direccion_destino_id)
        REFERENCES direcciones(id) ON DELETE RESTRICT
);

-- MODULO 4: SEGUIMIENTO, FINANZAS Y VALORACIONES

CREATE TABLE registro_etapa_delivery (
    id_registro SERIAL PRIMARY KEY,
    envio_id INT NOT NULL,
    etapa_alcanzada VARCHAR(50) NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    latitud DECIMAL(10,8) NOT NULL,
    longitud DECIMAL(11,8) NOT NULL,
    fue_valido INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_registro_envio FOREIGN KEY (envio_id)
        REFERENCES envios(id) ON DELETE CASCADE
);

CREATE TABLE historial_ganancia (
    id_ganancia SERIAL PRIMARY KEY,
    id_repartidor INT NOT NULL,
    envio_id INT NOT NULL UNIQUE,
    monto_liquidado DECIMAL(10,2) NOT NULL,
    fecha_liquidacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ganancia_repartidor FOREIGN KEY (id_repartidor)
        REFERENCES repartidor(id_repartidor) ON DELETE RESTRICT,
    CONSTRAINT fk_ganancia_envio FOREIGN KEY (envio_id)
        REFERENCES envios(id) ON DELETE RESTRICT
);

CREATE TABLE calificacion_delivery (
    id_calificacion SERIAL PRIMARY KEY,
    id_repartidor INT NOT NULL,
    envio_id INT NOT NULL UNIQUE,
    estrellas INT NOT NULL,
    comentario_anonimo TEXT,
    CONSTRAINT fk_calificacion_repartidor FOREIGN KEY (id_repartidor)
        REFERENCES repartidor(id_repartidor) ON DELETE CASCADE,
    CONSTRAINT fk_calificacion_envio FOREIGN KEY (envio_id)
        REFERENCES envios(id) ON DELETE CASCADE
);

CREATE TABLE notificacion_delivery (
    id_notificacion SERIAL PRIMARY KEY,
    id_repartidor INT,
    envio_id INT NOT NULL,
    tipo_canal VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    contenido_mensaje TEXT NOT NULL,
    estado_envio VARCHAR(50) NOT NULL DEFAULT 'SENT',
    fecha_notificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_repartidor FOREIGN KEY (id_repartidor)
        REFERENCES repartidor(id_repartidor) ON DELETE SET NULL,
    CONSTRAINT fk_notificacion_envio FOREIGN KEY (envio_id)
        REFERENCES envios(id) ON DELETE CASCADE
);
```

### 4. Cargar datos de prueba

```sql
INSERT INTO roles (id, nombre) VALUES
    (1, 'CLIENTE'),
    (2, 'REPARTIDOR'),
    (3, 'ADMINISTRADOR');

INSERT INTO vehiculo (id_vehiculo, tipo, descripcion) VALUES
    (1, 'MOTO', 'Motocicleta'),
    (2, 'AUTO', 'Automóvil'),
    (3, 'BICICLETA', 'Bicicleta'),
    (4, 'A PIE', 'Reparto a pie');
```

### 5. Configurar las credenciales de PostgreSQL

Configurá la conexión a la base de datos en el archivo de propiedades en `src/main/resources/application-secret.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
```

Definí las variables de entorno `DB_USERNAME` y `DB_PASSWORD` con las credenciales de tu entorno.

### 6. Verificar la configuración

Antes de iniciar la aplicación, revisá:

- La versión de Java configurada en el proyecto.
- Las dependencias de Spring Boot y el driver de PostgreSQL.
- La conexión a la base de datos.
- La estrategia de creación del esquema: scripts SQL, migraciones o generación de entidades.
- La configuración de autenticación y autorización.
- Las credenciales y variables de entorno de las integraciones externas, si están habilitadas.

### 7. Ejecutar el backend

Si el proyecto utiliza Maven, desde la carpeta raíz ejecutá:

```bash
mvn spring-boot:run
```

### 8. Comprobar que la aplicación funciona

Revisá los registros de inicio para confirmar que Spring Boot arrancó correctamente y que pudo conectarse a PostgreSQL.

La dirección local base es:

```text
http://localhost:8080
```

Las rutas concretas de la API dependen de los mappings definidos en los controladores. Consultá las anotaciones `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping` y `@DeleteMapping` del código fuente para conocer los endpoints disponibles. No se incluyen rutas específicas en la información de diseño recibida.

## Estados y reglas de negocio

La especificación contempla los siguientes estados generales para `envios`:

- `BUSCANDO_REPARTIDOR`: el envío todavía necesita una asignación.
- `ASIGNADO`: existe un repartidor asignado.
- `RETIRADO`: el paquete fue retirado del origen.
- `ENTREGADO`: el envío finalizó correctamente.
- `CANCELADO`: el envío se canceló.

Los nombres definitivos deben coincidir con las constantes y validaciones utilizadas por el código. Los eventos más detallados —por ejemplo, llegada al comercio o llegada al cliente— pueden registrarse en `registro_etapa_delivery` sin convertir cada evento necesariamente en un estado general independiente.

Otras reglas previstas en la especificación funcional:

- Un repartidor con un envío activo no debería aceptar otro al mismo tiempo.
- Una asignación sin respuesta debe expirar tras el plazo establecido, previsto en 5 minutos.
- La recogida y la entrega deben validarse con los códigos de seguridad correspondientes.
- Los códigos deben caducar según la política de seguridad definida, prevista en un máximo de 2 horas.
- Después de tres rechazos consecutivos en una misma jornada, la propuesta funcional contempla una pausa automática de 15 minutos y el cambio temporal a no disponible.
- Una liquidación debe generarse una sola vez por envío entregado.
- Las estrellas de una calificación deben limitarse al rango de 1 a 5.

## Requisitos no funcionales previstos

- **Usabilidad:** flujo de entrega guiado, acciones claras y teclado numérico para la introducción de códigos.
- **Rendimiento:** la especificación propone responder a una aceptación de pedido en menos de 2 segundos y validar tokens en menos de 1 segundo.
- **Seguridad:** contraseñas hasheadas, códigos protegidos y autorización de las operaciones según el usuario y su rol.
- **Caducidad:** códigos de recogida y entrega con una ventana máxima prevista de 2 horas; cierre de sesión después de 7 días de inactividad, si se implementa esa política.
- **Conectividad:** las operaciones transaccionales dependen de una conexión de datos activa; el modo offline transaccional no está incluido en el alcance inicial descrito.
- **Compatibilidad del cliente móvil:** la especificación contempla Android 8 o superior e iOS 13 o superior. La compatibilidad real dependerá de la aplicación móvil que se desarrolle.

## Alcance y limitaciones

La propuesta prioriza un flujo de logística sencillo para comercios y repartidores locales. Entre las limitaciones del alcance inicial se encuentran:

- El repartidor recibe pedidos mediante asignación directa del comercio; no navega una lista abierta de pedidos.
- La tarifa del flete se administra centralmente, no se negocia desde el perfil del repartidor.
- No se contempla visualizar datos de otros repartidores.
- No se prevén mapas integrados ni navegación GPS en vivo en la primera versión descrita.
- Una falta de conexión puede impedir validar una etapa hasta que se recupere el acceso al backend.

## Consideraciones para alinear el esquema y la implementación

El material de diseño contiene versiones sucesivas del modelo. Antes de considerar todas las funcionalidades como implementadas, conviene revisar estos puntos contra el código real:

1. **Códigos de recogida y entrega:** los requisitos los describen, pero el esquema SQL final mostrado no incluye columnas o una tabla específica para almacenar sus hashes, su expiración y su estado de uso.
2. **Autenticación telefónica:** los requisitos mencionan un acceso por teléfono y OTP, mientras que `usuarios` utiliza `email` único y `telefono` no tiene una restricción `UNIQUE`. La autenticación real debe documentar y aplicar un único criterio.
3. **Coordenadas:** `registro_etapa_delivery` exige latitud y longitud, aunque el alcance inicial excluye GPS en vivo. Debe definirse si las coordenadas se obtendrán solo para la auditoría de etapas o si esas columnas deben admitir valores nulos.
4. **Notificaciones generales:** `notificacion_delivery.envio_id` es obligatorio en el SQL, por lo que ese esquema no permite guardar una notificación sin un envío asociado, aunque la descripción funcional contempla alertas generales.
5. **Validaciones de base de datos:** el SQL mostrado no incluye un `CHECK` para limitar `calificacion_delivery.estrellas` a valores entre 1 y 5. Esa regla debe validarse en los servicios y, preferentemente, también en la base de datos.
6. **Fórmula de tarifas:** la tabla `tarifas_calculadas` ofrece parámetros de peso y volumen. La fórmula exacta del costo debe definirse en una única regla de negocio y documentarse de manera consistente.

Estas observaciones no impiden iniciar el proyecto, pero deben resolverse para que la documentación, la base de datos y los servicios del backend describan el mismo comportamiento.

## Endpoints de la API

Las rutas definitivas pueden encontrarse en `src/main/resources/endpoints`
