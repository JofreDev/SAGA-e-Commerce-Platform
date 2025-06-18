> [!IMPORTANT]  
> El MVP desarrollado cuenta con **3 grandes casos de uso** que fueron seleccionados cuidadosamente para mostrar cómo la lógica de negocio se define de manera clara y mantenible mediante una arquitectura limpia, al tiempo que se apalanca el potencial de distintas tecnologías y patrones arquitectónicos modernos como microservicios, EDA (Event-Driven Architecture) y el patrón SAGA de coreografía.
>
> 1. **Lectura de información desde la base de datos (GET):**
>    Este primer caso de uso, aunque clásico, permite ilustrar la separación de responsabilidades entre las capas de acceso a datos, dominio y aplicación. La consulta viaja por la arquitectura limpia y expone la flexibilidad para extender o modificar las reglas de negocio sin comprometer la infraestructura.
>
> 2. **Creación de una nueva orden de compra :**
>    A primera vista, este caso puede parecer trivial, pero detrás de la creación de una orden de pago se esconde una lógica de negocio compleja: validaciones, descuentos, verificación de stock, cálculos de totales, manejo de errores y disparo de eventos. Aquí se ve cómo la arquitectura limpia permite orquestar esta lógica central mientras los detalles tecnológicos (p. ej. microservicios, colas de eventos) permanecen desacoplados.
>
> 3. **Compensación (coreografía SAGA):**
>    El caso más interesante es, sin duda, el manejo de la compensación típica de una SAGA coreografiada. Por ejemplo, cuando una orden de compra ya fue creada y el stock fue descontado, pero por algún motivo el pago falla y es necesario revertir la operación. Acá lo interesante es que la reversión no es un simple rollback en cadena, sino que se realiza a través de la recepción y procesamiento de eventos asíncronos. Esto implica lógica de negocio sofisticada: determinar qué acciones compensatorias ejecutar, en qué orden, cómo manejar la idempotencia y asegurar la consistencia eventual.
>
> Estos tres casos de uso no solo muestran la lógica "pura y dura" de negocio en acción, sino que demuestran cómo una arquitectura bien diseñada permite que el sistema evolucione, escale y se adapte fácilmente a nuevos requerimientos o tecnologías.

> [!NOTE] 
> 1. Query de informacion - GET -
> A continuación puede ver algunas de las querys que puede realizar :
> * getAllProducts : `localhost:8089/api/products/all`
>   
>   <img src="https://github.com/user-attachments/assets/833d74a5-4f98-4998-9355-cce1f10a362e" width="500"/>
>
> * getProduct
>
>   <img src="https://github.com/user-attachments/assets/e5367492-4aef-40a8-ac89-4a3564b87c24" width="500"/>
>
> * getProductsByCategory
>
>   <img src="https://github.com/user-attachments/assets/3dc7bb00-b649-4538-ad68-4483e40b0023" width="500"/>
>
> * getAllCategories
>
> * <img src="https://github.com/user-attachments/assets/1214c3f8-cfe0-4643-885c-a7f9e983fa61" width="500"/>
>

> [!WARNING]
> Observabilidad a nivel de este primer caso de uso (Querys GET)
> 
> ![image](https://github.com/user-attachments/assets/43fce896-1863-42af-9abb-b24005d08852)
>
> Como se puede apreciar, entra por el endpoint GET y ejecuta las operaciones correspondientes.
> A nivel interna de arquitectura, en verdad, hace más saltos a nivel de capas.
>

> [!NOTE] 
> 2. Creación de una nueva orden de compra (coreografía SAGA)
> Para la creación de una nueva orden de compra se usará el mock `ms_commerce-api_service _mock` propuesto. Si desea tambien puede enviar mensajes desde el mismo broker (rabbit)
> 
>  ```curl
>  
>  curl --location 'localhost:8080/commerce-gateway/api/mock/sendOrder' \
> --header 'Content-Type: application/json' \
> --data '
> {
>   "clientId": "client-123",
>   "date": "2025-06-15T10:30:00",
>   "paymentMethod": "CREDIT_CARD",
>   "comment": "Primera compra con descuento",
>   "state": "REQUEST",
>   "items": [
>     {
>       "productId": 1,
>       "quantity": 2
>     },
>     {
>       "productId": 2,
>       "quantity": 1
>     }
>  ]
> }
> '
>  ```

>[!IMPORTANT]
> Como se puede observar, se va a ejecutar una peticion http pero esto solo es con fines didacticos y para que sea facil de probar. Este mock por dentro lo que hace es convertir esta peticion en un mensaje de rabbit y enviarlo de manera asincrona a `ms_product-management_service`
> y luego el mismo mock se queda escuchando la respuesta que de `ms_product-management_service`. Basicamente se implementa un `patron reqReply` en el mock que con el fin de que cumpla su rol de simular al API gateway propuesto en el diseño oficial.
> 
> 
>   * Envio de la petición
>   
>  <img src="https://github.com/user-attachments/assets/2e36a5cb-a689-471f-82e9-03726c84d478" width="700"/>
>
> Como se puede observar se devuelve un JSON totalmente enriquecido y con un estado de orde de compra "PENDING", estado que a su vez queda guardado a nivel de base de datos hasta confirmación de que paso con esa orden.
>
> * logs del servicio :
>   
>   ![image](https://github.com/user-attachments/assets/bac304e5-a32b-4196-a4f7-6fe1e015a0ac)
>
> Como se puede evidenciar :
>   1. el Stock de los productos relacionados en la orden es automaticamente modificado a nivel de base de datos
>   2. Se crean 2 nuevos items de compra con id 7 y 8 respectivamente
>   3. Por ultimo como se vio en la respuesta de la primera imagen, tambien se crea la orden de comrpa que contiene estos items y que queda en estado "PENDING" 
> 

> [!WARNING]
>  Observabilidad a nivel de generación de Orden de compra (Purchase Order)
>
> ![image](https://github.com/user-attachments/assets/51280902-611f-4d29-9047-d78de158cd94)
>
> Como se puede observar en la anterior imagen :
> 1.  La petición de compra ingresa de forma asincrona al servicio.
> 2.  Son muchoas más los pasos que se deben realizar para genrar una orden de compra de manera consistente. (Tanto logica de negocio como operaciones a nivel de Base de datos)
>
> ## ¿ Qué sucede si falla ?
>  Lo haremos entrar en error !!
>
> De manera muy sencilla podemos poner un stock muy grande. Esto es algo que en el disño oficial que plantee nunca sucederia. Nunca llegarai una petición hasta este punto con un stock desbordado. Más sin embargo el microservicio está preparado para cualquier caso de error.
> 
> Se le envia la siguiente petición (775) en cantidad de unidades que se piensan pedir. Cantidad que no existey que por tanto hará entrar en fallo a la logica de generación de Orden de compra:
> 
> ![image](https://github.com/user-attachments/assets/c7a8555e-552e-449a-822c-fc82bc9a5ad4)
>
> ¿ El resultado ?
>
> ![image](https://github.com/user-attachments/assets/f6db987f-a1d2-4ab6-a2f7-975c599419d5)
>
> ¿ Qué sucede ? ¿ Acaso parece que el servicio no está manejando los posibles errores que puedan suceder ?
> No !! Recordemos que este cliente al que le estamoe enviado las peticiones es solo un mock que convierte http a MQ y los mismo de lado contrario !!
> Lo que hace el `ms_product-management_service` es lo que se debe hacer cuando un microservicio falla en un punto de una transacción en una arquitectura orientada a eventos y es enviar un evento fallback !!
>
> ¿ Qué sucede si vamos a revisar los mensajes de broker ?
>
> ![image](https://github.com/user-attachments/assets/2197e3dc-0018-42c2-9df6-8c75cb4a84d1)
>
> Guala !!! Nueva cola se genera para reportar errores a quien tenga que escucharlos y darle manejo. 






> [!NOTE] 
> 3.  Compensación asincronica de una transacción fallida (coreografía SAGA) 
