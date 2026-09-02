# Cobre Event Notification System & Self-Service API

Este proyecto implementa un sistema robusto, escalable y seguro para el envío de eventos (Webhooks) a clientes y provee una API REST de autoservicio para que los clientes puedan auditar y reintentar sus notificaciones.

Esta solución fue diseñada para cumplir con los más altos estándares de calidad, resiliencia y separación de responsabilidades, pensada para un entorno de alta concurrencia.

---

## 1. Arquitectura de Software

El proyecto fue construido utilizando **Arquitectura Hexagonal (Puertos y Adaptadores)**. 

### ¿Por qué Arquitectura Hexagonal?
1. **Aislamiento del Dominio:** La lógica de negocio principal (reglas de reintentos, firmas de seguridad, estados de notificación) vive en el centro (`domain`) y no tiene dependencias de frameworks externos como Spring, RabbitMQ o bases de datos relacionales.
2. **Testabilidad:** Al tener las dependencias invertidas a través de puertos (`ports`), el dominio central es extremadamente fácil de testear mediante mocks.
3. **Flexibilidad:** Permite cambiar la base de datos (ej. de PostgreSQL a MongoDB) o el motor de colas (ej. de RabbitMQ a Kafka) creando nuevos adaptadores en la capa de infraestructura sin tocar una sola línea de código del dominio.

---

## 2. Componentes del Sistema

La solución está completamente contenerizada (`Docker`) y orquestada para funcionar "On-Premise" o en cualquier nube mediante `docker-compose`.

* **Java 21 LTS & Spring Boot 3.3:** Lenguaje y framework base. Se beneficia de *Virtual Threads* (Loom) para manejar alta concurrencia en I/O (llamadas HTTP de Webhooks) sin bloquear hilos del sistema operativo.
* **PostgreSQL:** Base de datos relacional para la persistencia transaccional del estado de los eventos (`PENDING`, `COMPLETED`, `FAILED`).
* **RabbitMQ:** Motor de mensajería asíncrona. Desacopla la recepción del evento de su despacho por red. Implementa patrón de reintentos y una **Dead Letter Queue (DLQ)**.
* **NGINX:** Actúa como API Gateway y Reverse Proxy. Provee seguridad perimetral limitando la cantidad de peticiones (Rate Limiting) para evitar abusos o ataques DDoS.

---

## 3. Decisiones de Diseño y Patrones Implementados

* **Resiliencia (Retry Pattern + Exponential Backoff):** Las notificaciones que fallan (por timeout o HTTP 5xx del cliente) no se descartan inmediatamente. Se reintentan hasta un máximo de 5 veces aplicando "Exponential Backoff con Jitter" para no saturar al cliente.
* **Dead Letter Queue (DLQ):** Si tras los 5 reintentos el mensaje sigue fallando, se envía de forma segura a una cola de mensajes muertos (DLQ) para auditoría o reproceso manual posterior, asegurando que no haya pérdida de datos.
* **Seguridad y Confianza (HMAC-SHA256):** Toda notificación enviada al cliente lleva un hash criptográfico del payload (`X-Cobre-Signature`). Esto permite al cliente verificar que el mensaje provino auténticamente del sistema y no fue alterado en tránsito.
* **Seguridad de la API (Multitenancy):** La API REST está protegida, garantizando que el `CLIENT001` solo pueda ver y reintentar sus propias notificaciones. (*Nota: Para facilitar las pruebas locales, se habilitó un fallback que permite simular la sesión enviando el header `X-Client-Id` directamente*).
* **Manejo Global de Excepciones:** Se implementó un `@RestControllerAdvice` para centralizar los errores y devolver respuestas JSON estándar (HTTP 400, 404, 500) evitando fugar trazas de excepciones.

---

## 4. Estructura del Proyecto

```text
src/main/java/com/cobre/notification/
├── domain/                      # Capa central (Lógica de negocio pura)
│   ├── model/                   # Entidades del dominio (NotificationEvent, etc.)
│   ├── port/                    # Interfaces (Inbound y Outbound)
│   └── service/                 # Casos de uso y reglas de negocio
├── infrastructure/              # Capa externa (Frameworks, DB, Mensajería)
│   ├── adapter/in/              # Controladores REST y Consumidores RabbitMQ
│   ├── adapter/out/             # JPA Repositories, Clientes HTTP Webhook
│   └── config/                  # Configuración de Spring, Seguridad, Inicializador de datos
```

---

## 5. Requisitos Previos

* Tener instalado **Docker** y **Docker Compose**.
* (Opcional) Git, curl, Postman para pruebas.
* Los puertos `80`, `5432`, `5672`, y `15672` deben estar libres en tu máquina local.

---

## 6. Cómo Desplegar y Ejecutar

El proyecto incluye un mecanismo de "Data Seeding". Al iniciar por primera vez, un componente poblará automáticamente la base de datos PostgreSQL con 10 registros de prueba leídos de `src/main/resources/data/notification_events.json`.

1. **Clonar el repositorio y entrar a la carpeta:**
   ```bash
   git clone https://github.com/shadoweagle1228/prueba-cobre.git
   cd prueba-cobre
   ```

2. **Levantar la infraestructura con Docker Compose:**
   Este comando descargará las imágenes, compilará el código de Spring Boot en un contenedor temporal de Maven (multi-stage build) y levantará los servicios (App, BD, Cola, Proxy NGINX).
   ```bash
   docker-compose up -d --build
   ```

3. **Verificar el estado de los contenedores:**
   ```bash
   docker-compose ps
   ```
   *Deberías ver a `cobre-db`, `cobre-rabbitmq`, `cobre-app`, y `cobre-nginx` en estado `Up / Healthy`.*

---

## 7. Cómo Probar la Aplicación (API REST)

Las peticiones HTTP pasan por el NGINX (puerto `80`), el cual las redirige internamente a la aplicación Spring Boot.

Para simular la autenticación de un cliente en el entorno de pruebas local, se debe enviar el Header HTTP `X-Client-Id: CLIENT001` (o `CLIENT002`, `CLIENT003`).

### 7.1. Obtener Historial de Notificaciones (Con Filtros Paginados)
Permite a un cliente consultar el estado de sus notificaciones con filtros opcionales.

**Petición (cURL en Windows / Linux):**
```bash
curl -s -H "X-Client-Id: CLIENT001" "http://localhost/notification_events?page=0&size=5"
```

**Respuesta Esperada (JSON):**
Retornará un objeto paginado de Spring (`Page<T>`) con los eventos de pago y transferencias asociadas al cliente, ordenados por fecha.

### 7.2. Obtener un Evento Específico
**Petición:**
```bash
curl -s -H "X-Client-Id: CLIENT001" "http://localhost/notification_events/EVT001"
```

### 7.3. Reintentar Manualmente una Notificación
Si una notificación falló, el cliente puede forzar su reintento a través de la API. Este endpoint cambia el estado a `PENDING` y encola el mensaje nuevamente en RabbitMQ.

**Petición:**
```bash
curl -X POST -H "X-Client-Id: CLIENT002" "http://localhost/notification_events/EVT003/retry"
```

---

## 8. Consideraciones Finales y Futuras Mejoras

Pensando en un entorno de **Producción real**, se recomiendan las siguientes mejoras:

1. **Eliminar el Fallback de Seguridad:** El filtro de autenticación actualmente permite simular sesión si solo se envía el header `X-Client-Id`. En producción, debe hacerse cumplir de manera estricta la firma criptográfica del Token JWT.
2. **Circuit Breaker:** Agregar un patrón Circuit Breaker (ej. Resilience4j) en el adaptador de salida HTTP (Webhook) para evitar agotar hilos del sistema si un servidor de un cliente se cae por completo y los timeouts se empiezan a acumular.
3. **Manejo de Secretos:** Integrar un gestor de secretos (Vault o AWS Secrets Manager) en lugar de exponer o quemar variables de entorno en el `docker-compose.yml`.
4. **Trazabilidad Distribuida:** Integrar OpenTelemetry o Zipkin para rastrear el ciclo de vida de un `event_id` desde que entra a la API hasta que sale por el Webhook pasando por las colas de RabbitMQ.
