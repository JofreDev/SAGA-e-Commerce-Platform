# reactor-rabbitMQ

> [!TIP]
> This lab implements the following use case :
> >
> ![image](https://github.com/user-attachments/assets/91ebb17c-efe2-4093-ae8d-4af7b894e8e0)
> >
> Considerations :
> > * The producer and the consumer are implemented in the same project with pragmatic aims
> > * CommandLineRunner is implemented to facilitate the execution of message sending and receiving.

> [!IMPORTANT]
> Test project
>  > requirements
>  > * SDK Java 21
>  > * Docker
>  > * Gradle 8 or major
>  >
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
> > 3. run reactor-spring application
> > logs :
> > ```text
> >   .   ____          _            __ _ _
> > /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
> >( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
> > \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
> >  '  |____| .__|_| |_|_| |_\__, | / / / /
> > =========|_|==============|___/=/_/_/_/
> >
> > :: Spring Boot ::                (v3.5.0)
> >
> >2025-06-13T18:41:28.757-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] c.r.demo.RabbitWorkshopApplication       : Starting RabbitWorkshopApplication using Java 21.0.7 with PID 31296 (C:\Users\User\Documents\Meli\rabbitMQ _demo\build\classes\java\main started by User in C:\Users\User\Documents\Meli\rabbitMQ _demo)
> >2025-06-13T18:41:28.761-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] c.r.demo.RabbitWorkshopApplication       : No active profile set, falling back to 1 default profile: "default"
> >2025-06-13T18:41:28.803-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
> >2025-06-13T18:41:28.803-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
> >2025-06-13T18:41:29.758-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
> >2025-06-13T18:41:29.905-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080 (http)
> >2025-06-13T18:41:29.909-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] c.r.demo.RabbitWorkshopApplication       : Started RabbitWorkshopApplication in 1.463 seconds (process running for 1.763)
> >2025-06-13T18:41:30.049-05:00  INFO 31296 --- [Rabbit-workshop] [  restartedMain] reactor.rabbitmq.Receiver                : Consumer amq.ctag-0JHYifwC2VNE8dGEJY4EsA consuming from demo-queue has been registered
> >2025-06-13T18:41:31.065-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:31.066-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-4] c.r.demo.RabbitWorkshopApplication       : Received message Message - 1
> >2025-06-13T18:41:32.067-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:32.067-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-5] c.r.demo.RabbitWorkshopApplication       : Received message Message - 2
> >2025-06-13T18:41:33.069-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:33.069-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-6] c.r.demo.RabbitWorkshopApplication       : Received message Message - 3
> >2025-06-13T18:41:34.075-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:34.075-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-7] c.r.demo.RabbitWorkshopApplication       : Received message Message - 4
> >2025-06-13T18:41:35.077-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:35.077-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-8] c.r.demo.RabbitWorkshopApplication       : Received message Message - 5
> >2025-06-13T18:41:36.081-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:36.081-05:00  INFO 31296 --- [Rabbit-workshop] [pool-8-thread-9] c.r.demo.RabbitWorkshopApplication       : Received message Message - 6
> >2025-06-13T18:41:37.081-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:37.082-05:00  INFO 31296 --- [Rabbit-workshop] [ool-8-thread-10] c.r.demo.RabbitWorkshopApplication       : Received message Message - 7
> >2025-06-13T18:41:38.094-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:38.094-05:00  INFO 31296 --- [Rabbit-workshop] [ool-8-thread-11] c.r.demo.RabbitWorkshopApplication       : Received message Message - 8
> >2025-06-13T18:41:39.099-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:39.099-05:00  INFO 31296 --- [Rabbit-workshop] [ool-8-thread-12] c.r.demo.RabbitWorkshopApplication       : Received message Message - 9
> >2025-06-13T18:41:40.101-05:00  INFO 31296 --- [Rabbit-workshop] [   rabbitmq-nio] c.r.demo.RabbitWorkshopApplication       : msg sent - true
> >2025-06-13T18:41:40.101-05:00  INFO 31296 --- [Rabbit-workshop] [ool-8-thread-13] c.r.demo.RabbitWorkshopApplication       : Received message Message - 10
> >
>  ![image](https://github.com/user-attachments/assets/ed5dc5ff-d7ea-42d8-9079-0bf2ab671dc7)
> As you can see, the sender starts sending messages every second and the consumer intercepts them every time they are published in the queue.
