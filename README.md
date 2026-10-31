# Practica 8
Rebeca Moreno González


## Objetivo

Aprender a conectar una aplicación Java con PostgreSQL mediante JDBC y a trabajar con datos en formato JSON dentro de la base de datos.

## Decisiones

En esta actividad se desarrolló una aplicación Java conectada a PostgreSQL mediante JDBC. Se creó una clase de conexión para centralizar el acceso a la base de datos y se utilizaron objetos PreparedStatement para ejecutar consultas de forma segura, evitando problemas de inyección SQL. También se empleó ResultSet para recuperar y mostrar la información almacenada en las tablas. Además, se creó una tabla con una columna de tipo JSONB para almacenar datos en formato JSON y se realizaron consultas sobre sus atributos utilizando las funciones de PostgreSQL.

Para el reto, se amplió la aplicación para gestionar una tienda en línea aprovechando las tablas de clientes, productos, pedidos y detalles de pedido ya existentes. Se añadió una columna JSONB para almacenar preferencias adicionales de los clientes, permitiendo guardar información flexible sin modificar la estructura principal de la tabla. Asimismo, se implementaron consultas para obtener estadísticas de compras por cliente mediante operaciones de agrupación y cálculo de totales.

## Pruebas realizadas

**Para la actividad:**

- Establecí la conexión entre la aplicación Java y PostgreSQL mediante JDBC para comprobar que el acceso a la base de datos se realizara correctamente.
- Ejecuté una inserción en la tabla empleado utilizando PreparedStatement para verificar que los registros se almacenaran correctamente y evitar problemas de inyección SQL.
- Realicé consultas sobre la tabla empleado utilizando ResultSet para comprobar que los datos pudieran recuperarse y mostrarse correctamente desde la aplicación.
- Creé la tabla mensajes con una columna de tipo JSONB e inserté registros en formato JSON para validar el almacenamiento de datos semiestructurados.
- Ejecuté consultas sobre la columna contenido utilizando los operadores JSON de PostgreSQL para verificar la extracción de atributos específicos, como el nombre del usuario.

**Para el reto:**

- Registré nuevos clientes desde la aplicación y comprobé que los datos se almacenaran correctamente en la tabla cliente.
- Agregué preferencias de clientes en formato JSONB y realicé consultas para verificar que la información pudiera recuperarse correctamente.
- Registré nuevos pedidos asociados a clientes para comprobar que quedaran almacenados de forma correcta en la tabla pedido.
- Finalmente, ejecuté consultas de estadísticas para calcular el total de compras por cliente y verifiqué que los resultados coincidieran con la información registrada en los pedidos y productos.

## Errores

El principal error que tuve al realizar esta práctica fue `No suitable driver found for jdbc:postgresql://localhost:5432/practica8`, el cual ocurre cuando Java no encuentra el controlador JDBC de PostgreSQL necesario para establecer la conexión con la base de datos. Este problema se solucionó descargando el archivo .jar correspondiente al driver de PostgreSQL y agregándolo a las bibliotecas del proyecto para que pudiera ser reconocido durante la ejecución.

## Conclusión

En esta práctica aprendí por primera vez a conectar una base de datos con Java utilizando JDBC, algo que no había realizado antes. Al principio se me complicó un poco configurar la conexión y solucionar algunos errores, pero al realizar la actividad pude comprender mejor cómo Java puede comunicarse con PostgreSQL para insertar, consultar y manejar información. También aprendí a trabajar con datos en formato JSONB. Esta práctica me ayudó a entender mejor cómo se pueden integrar las bases de datos con una aplicación Java.
