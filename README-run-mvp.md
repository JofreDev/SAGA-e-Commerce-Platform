# SAGA-e-Commerce-Platform project
>
> [!IMPORTANT]
> Test project
>  > requirements
>  > * SDK Java 21
>  > * Docker
>  > * Gradle 8 or major
>  > * IntelliJ IDEA
>  >

> [!NOTE]  
> Steps to execute
> > 1. get rabbitmq
> > ```bash
> > # latest RabbitMQ 4.x
> > docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
> > ```
> > 2. go to http://localhost:15672/
> > * passw and user are guest
> > 
> > ![image](https://github.com/user-attachments/assets/2850d5da-5fc5-4296-98d1-709eb4731356)
> >  <br><br>
> > ![image](https://github.com/user-attachments/assets/19cc97fc-44e1-4dc5-a096-2a16be247d44)
> > 
> > 3. run API GATEWAY(commerce-api) (mock)
> >   
> >   ![image](https://github.com/user-attachments/assets/592843ce-b2db-4d15-985c-7f90248f5190)
> >
> > * You can send a couple of requests to test the connection to rabbit but you will not get any response as they will be queued in the queue. So let's wait for a real test.
> >   
> > 4. run ms_product-management_service
> >
> > ![image](https://github.com/user-attachments/assets/40071b49-3763-440b-83f7-26d5beb19e51)
> > 
> > * If there are no messages queued in the input queue then you will only see the microservice startup logs.
> > 
> > * On the other hand, if for some reason there were messages already glued... 
> > 
> > ![image](https://github.com/user-attachments/assets/05a2214c-92b3-4dd5-a51d-77abf2a4d6ae)
> > As you can see, it is already starting to consume the previously pasted messages. Therefore, we can already see a resilience to failures (for example, if this microservice were to fall momentarily).

> [!TIP]
> > ## Activate observability (opentelemetry)
> > The project has enabled observability of distributed traces and response times per request.
> >  * Opentelemetry and Jaeger (Backend) (open source, distributed tracing platform)
> > ![image](https://github.com/user-attachments/assets/556605aa-e039-4e51-8548-b9fbe3dfde47)
> >
> > 5. (OPTIONAL, only if you want to activate observability) get  [jaeger](https://www.jaegertracing.io/docs/1.6/getting-started/)
> > 
> > 6. go to [http://localhost:15672/](http://localhost:16686/search)
> > 
> > 7. Search service
> > ![image](https://github.com/user-attachments/assets/1d70a8fc-2708-4d3d-a746-df8356661266)
> > In this case choose the productManagement microservice and you can already see the traces from the operations performed. In this case a GET of products by category.
> > 
> > ![image](https://github.com/user-attachments/assets/6c69a320-e3d7-46f4-814b-0781df6399ff)
> >
>  > **🔍 Trace Summary**
>  > 
>  > - **Endpoint traced**: `GET /api/products/category/{idCategory}` under the `productManagement` service.
>  > - **Total request duration**: **10.23 ms**.
>  > - **Spans observed**:
>  >   - `CategoryCrudRepository.findById` → **2.56 ms**
>  >   - `ProductCrudRepository.findAllByCategoryId` → **5.11 ms**
>  > - **Executed SQL query**:
>  >   - `SELECT CATEGORY.* FROM CATEGORY WHERE CATEGORY.categoryId = $1 LIMIT ?`
>  > - **Database**: `testdb` (H2 in-memory).
>  > - **Database user**: `sa`.
>  > - **System info**: Windows 11 (`host.name = Arkantos043`).
>  > - **JDK**: Amazon Corretto 21.
>  > - **Telemetry agent**: OpenTelemetry 2.16.0.
>  > - **Execution thread**: `reactor-http-nio-5`.
>  > 
>  > **✅ What This Trace Provides**
>  > 
>  > - Complete end-to-end tracing from HTTP request to database call.
>  > - Precise span durations for internal operations.
>  > - Visibility into executed SQL for auditing and performance tuning.
>  > - Connection metadata: DB, host, system architecture.
>  > - Trace ID correlation across microservices.
>  > - Shows which thread handled each operation.
>  > 
>  > **🛠️ Use Cases**
>  > 
>  > - Identifying performance bottlenecks.
>  > - Distributed system debugging and tracing.
>  > - Monitoring SQL query patterns.
>  > - Production-level root cause analysis.
>  > - Enhancing observability and reliability across services.
> >  In this case choose the productManagement microservice and you can already see the traces from the operations performed. In this case a GET of products by category.
> >
> > 4. This example shows one of the most complex operations at the business logic level, when a new purchase order is created.
>  >
> > ![image](https://github.com/user-attachments/assets/b3f8ce88-c450-4534-bd84-373ddc442807)




