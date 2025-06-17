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
> > 4. run ms_product-management_service 