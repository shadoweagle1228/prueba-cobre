# Uso de Inteligencia Artificial (AI Usage Report)

Tal como se fomenta en la cultura y en las directrices de la prueba técnica de Cobre, este proyecto fue desarrollado adoptando herramientas de Inteligencia Artificial Generativa como un *Pair Programmer* para maximizar la productividad y garantizar mejores prácticas arquitectónicas.

A continuación se detalla cómo se integró la IA en el flujo de desarrollo:

## 1. Herramientas Utilizadas
- **Google Antigravity (AGY):** Plataforma de desarrollo con agentes autónomos impulsados por IA (Gemini).
- **Gemini (Large Language Model):** Utilizado para asistencia en código, generación de pruebas unitarias y validación de arquitectura.

## 2. Casos de Uso Específicos

### A. Andamiaje y Arquitectura Base
**Descripción:** La IA se utilizó para estructurar el *scaffolding* inicial del proyecto basándose estrictamente en el patrón de **Arquitectura Hexagonal**.
**Prompts / Instrucciones Clave:** 
> "Por favor, inicializa un proyecto Spring Boot con Java 21 y aisla los componentes usando Arquitectura Hexagonal. Crea las carpetas `domain/model`, `domain/port`, `infrastructure/adapter/in`, y `infrastructure/adapter/out`."

### B. Generación de Pruebas Unitarias (Test-Driven)
**Descripción:** Se delegó la escritura del código repetitivo (Boilerplate) de las pruebas unitarias a la IA para alcanzar y superar el 80% de cobertura (JaCoCo).
**Prompts / Instrucciones Clave:**
> "Genera tests unitarios utilizando JUnit 5 y Mockito para la clase `DeliveryService`. Asegúrate de cubrir los casos de éxito, los reintentos (Exponential Backoff) y el encolamiento hacia la DLQ tras fallar 5 veces."

### C. Configuración de Infraestructura (Docker & RabbitMQ)
**Descripción:** La IA ayudó a escribir y afinar el archivo `docker-compose.yml`, configurando los volúmenes, la red interna y la interconexión entre la API, PostgreSQL, NGINX y RabbitMQ.
**Prompts / Instrucciones Clave:**
> "Necesito contenerizar la aplicación. Crea un `docker-compose.yml` que incluya PostgreSQL, RabbitMQ, un proxy reverso NGINX y la imagen de Spring Boot construida en múltiples etapas (multi-stage) con Maven."

### D. Resolución de Bugs (Troubleshooting)
**Descripción:** La IA fue clave para analizar logs en tiempo real (por ejemplo, errores de *type casting* en Spring Data JPA con PostgreSQL) y proponer soluciones rápidas sin necesidad de ir a foros de desarrollo.
**Prompts / Instrucciones Clave:**
> "El contenedor `cobre-db` falló porque PostgreSQL arrojó el error `could not determine data type of parameter $2` en esta query JPA: `(:startDate IS NULL OR e.deliveryDate >= :startDate)`. ¿Cómo lo solucionamos?"

## 3. Conclusión sobre la Productividad
El uso del agente autónomo permitió reducir los tiempos de andamiaje y DevOps en más de un 60%, permitiendo focalizar el esfuerzo cognitivo humano en la calidad del diseño arquitectónico, el modelado del dominio y las consideraciones de seguridad perimetral (OWASP).
