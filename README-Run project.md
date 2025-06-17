# SAGA-e-Commerce-Platform project
>
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
> > 3. run API GATEWAY(commerce-api) (mock)
> > * When you run the mock you will see that the first thing it does is to send messages to a request queue.
> >   
> >   ![image](https://github.com/user-attachments/assets/ea7f63bd-ca71-47bc-8422-b0816484b56a)
> > * If you check in the broker (rabbit) you can see the messages sent and by which queue they were sent.
> > 
> > * ![image](https://github.com/user-attachments/assets/8b9d9c2c-6eed-47a8-9134-73a52262df75)
> > 5. run ms_product-management_service
> > * As you can see, it is already starting to consume the previously pasted messages. Therefore, we can already see a resilience to failures (for example, if this microservice were to fall momentarily).
> > 
> >   ![image](https://github.com/user-attachments/assets/05a2214c-92b3-4dd5-a51d-77abf2a4d6ae)
