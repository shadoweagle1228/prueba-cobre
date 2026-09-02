# Cobre Event Notification System & Self-Service API

Sistema de entrega de notificaciones transaccionales (Webhooks) y API REST de Autoservicio diseñado bajo **Arquitectura Hexagonal (Puertos y Adaptadores)** e infraestructura containerizada **On-Premise con Docker y Docker Compose**.

## 🚀 Tecnologías

- **Lenguaje:** Java 21 LTS
- **Framework:** Spring Boot 3.3.2 (con Virtual Threads)
- **Base de Datos:** PostgreSQL (containerizado)
- **Mensajería & Reintentos:** RabbitMQ + Dead Letter Queue (DLQ / DLX)
- **Seguridad:** Spring Security, JWT (Multitenant por `client_id`), Firma HMAC-SHA256 (`X-Cobre-Signature`)
- **Reverse Proxy:** NGINX con Rate Limiting
- **Orquestación:** Docker Compose

---

## 🛠️ Ejecución con Docker Compose

```bash
docker-compose up -d --build
```

Esto levantará los siguientes servicios:
- **App (Spring Boot):** `http://localhost:8080`
- **Reverse Proxy (NGINX):** `http://localhost:80`
- **PostgreSQL:** `localhost:5432`
- **RabbitMQ Management UI:** `http://localhost:15672` (guest / guest)

---

## 📡 Endpoints REST API de Autoservicio

Todos los endpoints requieren autenticación multitenant pasando la cabecera `X-Client-Id` o un Bearer Token JWT.

### 1. Consulta Masiva de Eventos
```http
GET /notification_events?startDate=2024-03-15T00:00:00Z&endDate=2024-03-15T23:59:59Z&deliveryStatus=completed&page=0&size=10
Header: X-Client-Id: CLIENT001
```

### 2. Consulta Detallada de Evento
```http
GET /notification_events/EVT001
Header: X-Client-Id: CLIENT001
```

### 3. Reintentar Entrega Manualmente (Replay)
```http
POST /notification_events/EVT003/replay
Header: X-Client-Id: CLIENT002
```

---

## 🔒 Seguridad (Security-by-Design & OWASP)

1. **OWASP A01 (Broken Access Control):** Cada cliente solo puede consultar y hacer *replay* de eventos asociados a su propio `client_id`.
2. **OWASP A02 (Cryptographic Failures):** Cada webhook saliente incluye en el header `X-Cobre-Signature` una firma HMAC-SHA256 calculada con el secreto del cliente.
3. **OWASP A03 (Injection):** Filtros de Spring Security y protección perimetral en NGINX.
