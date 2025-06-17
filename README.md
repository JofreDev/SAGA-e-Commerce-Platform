# SAGA-e-Commerce-Platform

**SAGA-e-Commerce-Platform** es un proyecto de e-commerce basado en microservicios, eventos y patrones de resiliencia distribuidos. 
Esta solución sigue principios de Clean Architecture, EDA, Asincronismo y Arquitectura orientada a microservicios, ademas está diseñada para escalar tanto funcional como técnicamente.


## Arquitectura General Propuesta

![draft2 0](https://github.com/user-attachments/assets/bfee587c-b807-44c4-90c7-798ab5f89ece)


## Componentes Principales

### 1. API Gateway (`commerce-api`)
- Orquestador de entrada.
- Encargado de autenticar peticiones mediante algun servicio de autenticación como Keycloak.
- Publica eventos al broker (solicitudes de compra, confirmaciones, fallos).
- Consulta `Redis` como caché de lectura para productos.
- Comunicación asincrónica con los demas componentes.

### 2. `order_management_service`
- Gestiona el ciclo de vida de la orden.
- Escucha eventos de solicitud de compra.
- Verifica stock a través de `product_management_service`.
- Publica eventos según el estado de la compra (éxito o fallo).
- Interactúa con el servicio de pagos vía eventos para enviar la petición de orden de pago.

### 3. `product_management_service`
- Gestión de catálogo e inventario.
- Emite eventos cuando hay cambios en productos o stock.
- Consulta y actualiza PostgreSQL.
- Notifica al gateway para refrescar caché en Redis.

### 4. `payment_gateway_service`
- Encargado de procesar pagos (Mercado Pago, PayU, PSE, etc).
- Implementa integración externa desacoplada mediante eventos.
- Reporta estado de pago (éxito o fallo) al sistema.

### 5. Redis
- Cache de solo lectura para mejorar tiempo de respuesta en catálogo.
- Actualizado mediante eventos publicados por `product_management_service`.


---

## ⚙️ Comunicación entre servicios

- La plataforma se comunica de forma **asíncrona** usando eventos publicados en un **bus de mensajería**.
- Se aplican principios de **resiliencia**:
  - Retry
  - Circuit Breaker
  - Timeouts
- Las operaciones críticas siguen el patrón **Saga**, donde los microservicios coordinan transacciones a través de eventos y compensaciones.

---

## Casos de error

Para mantener la claridad visual del diagrama, los flujos de error no se representan en su totalidad. Sin embargo, el sistema contempla:

- Eventos de fallo explícitos (`purchase_failed`, `payment_failed`, etc.).
- Reintentos con backoff.
- Operaciones idempotentes.
- Gestión de eventos compensatorios en caso de errores en cascada.

---
> [!IMPORTANT]
>## MVP actual
>
>### Stack tecnológico
>
>- Java 21 + Spring Boot 3
>   - Reactor
>   - WebFlux Framewor
>   - Reactor-rabbitmq
>   - H2 in memory DB 
>- Docker
>   - Image of RabbitMQ
>   - Image of Jaeger observability platform
>- Observability
>   - Opentelemetry Agent
>- Arquitectura del proyecto
>   - Arquitectura orientada a microservicios 
>   - Arquitectura basada en eventos (EDA) (Usando el broker de rabbitMQ)
>   - Patrón SAGA distribuido o de coreografía 
>- Arquitectura de los microservicios
>   - Clean Architecture
>
> ### El MVP implementa:
> - `product_management_service` : Para este MVP, se ha decidido unificar las responsabilidades de gestión de productos (`product_management_service`) e integración de órdenes de compra (`order_management_service`)  en un solo microservicio.
>    Esta consolidación simplifica el despliegue y la validación temprana, la cual nos ayuda a  representar el flujo completo del sistema sin introducir fragmentación innecesaria en esta etapa.
>    Gracias a la implementación basada en arquitectura limpia, la separación de responsabilidades sigue siendo estricta a nivel interno, asegurando que esta decisión no compromete la mantenibilidad
>    ni la futura escalabilidad del sistema. En caso de ser necesario, el servicio puede escindirse fácilmente en componentes independientes sin refactorizaciones profundas.
>    Por otro lado es importante recalcar que, la adopción de programación reactiva con Reactor y Webflux aporta resiliencia, trazabilidad y manejo centralizado de errores, 
>    facilitando la integración de los procesos sin perder calidad técnica ni modularidad.
>    > [!TIP] : Estos 2 componenetes son quienes, en verdad, tienen gran parte de la lógica de negocio o, como se denomina en Domain Driven Design, son quienes contienen mayor parte de la dificultad esencial.
>    > Es por tanto que se decide hacer la implementación más real sobre estos. 
> 
>    ![image](https://github.com/user-attachments/assets/4208bbae-cc1d-4b07-b770-312cdada8df4)
>   
> - `commerce-api (API Gateway)`  : En el diseño general, este componente funciona como un API Gateway puro, cuyo rol es enrutar solicitudes hacia los microservicios internos. Para el MVP, se ha decidido mockearlo directamente, ya que no contiene lógica de negocio relevante. Esto permite      > enfocarse en validar los flujos funcionales críticos sin sobrecargar la solución con infraestructura innecesaria en esta fase.
> [!NOTE]
>
>  Estos 2 microservicios representan la lógica principal del flujo de compra, permitiendo:
>    - Crear y gestionar órdenes
>    - Simular peticiones de orden de pagos por medio de colas
>    - Enviar eventos de éxito o fallo 
>    - Validar la viabilidad de una arquitectura basada en eventos y patrones SAGA

---

## Futuro

Este MVP será la base para integrar más microservicios en futuras versiones:
- `order_management_service`
- `shipping_service`
- `notification_service`
- `analytics_service`

---


