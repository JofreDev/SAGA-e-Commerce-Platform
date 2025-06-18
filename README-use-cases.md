> [!IMPORTANT]  
> El MVP desarrollado cuenta con **3 grandes casos de uso** que fueron seleccionados cuidadosamente para mostrar cómo la lógica de negocio se define de manera clara y mantenible mediante una arquitectura limpia, al tiempo que se apalanca el potencial de distintas tecnologías y patrones arquitectónicos modernos como microservicios, EDA (Event-Driven Architecture) y el patrón SAGA de coreografía.
>
> 1. **Lectura de información desde la base de datos (GET):**
>    Este primer caso de uso, aunque clásico, permite ilustrar la separación de responsabilidades entre las capas de acceso a datos, dominio y aplicación. La consulta viaja por la arquitectura limpia y expone la flexibilidad para extender o modificar las reglas de negocio sin comprometer la infraestructura.
>
> 2. **Creación de una nueva orden de compra (POST):**
>    A primera vista, este caso puede parecer trivial, pero detrás de la creación de una orden de pago se esconde una lógica de negocio compleja: validaciones, descuentos, verificación de stock, cálculos de totales, manejo de errores y disparo de eventos. Aquí se ve cómo la arquitectura limpia permite orquestar esta lógica central mientras los detalles tecnológicos (p. ej. microservicios, colas de eventos) permanecen desacoplados.
>
> 3. **Compensación (coreografía SAGA):**
>    El caso más interesante es, sin duda, el manejo de la compensación típica de una SAGA coreografiada. Por ejemplo, cuando una orden de compra ya fue creada y el stock fue descontado, pero por algún motivo el pago falla y es necesario revertir la operación. Acá lo interesante es que la reversión no es un simple rollback en cadena, sino que se realiza a través de la recepción y procesamiento de eventos asíncronos. Esto implica lógica de negocio sofisticada: determinar qué acciones compensatorias ejecutar, en qué orden, cómo manejar la idempotencia y asegurar la consistencia eventual.
>
> Estos tres casos de uso no solo muestran la lógica "pura y dura" de negocio en acción, sino que demuestran cómo una arquitectura bien diseñada permite que el sistema evolucione, escale y se adapte fácilmente a nuevos requerimientos o tecnologías.

> [!NOTE] 
> 1. Query de informacion - GET -
>

> [!NOTE] 
> 2. Creación de una nueva orden de compra (coreografía SAGA) 

> [!NOTE] 
> 3.  Compensación asincronica de una transacción fallida (coreografía SAGA) 
