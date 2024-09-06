
# Proyecto de Microservicios - Gestión de Archivos

## Propósito

Este proyecto tiene como objetivo implementar una arquitectura de microservicios utilizando **Java Spring Boot** para gestionar archivos. Cada microservicio tiene su propia base de datos, pero comparten un volumen común para el almacenamiento de archivos. Los servicios están orquestados utilizando **Docker Compose**, y el proyecto incluye componentes adicionales para la sincronización, descubrimiento de servicios, y balanceo de carga.

### Componentes Clave
1. **File Server Microservice**: Servicio dedicado a la gestión de archivos, desarrollado en **Java Spring Boot** y utilizando **PostgreSQL** para almacenar las rutas de los archivos. Los archivos se almacenan en un volumen compartido.
2. **Eureka Server**: Utilizado para el descubrimiento de servicios. Todas las instancias de los microservicios se registran en Eureka para permitir la detección automática.
3. **API Gateway**: Implementado con **Zuul** (o **Spring Cloud Gateway**), este servicio expone una única entrada a los microservicios, manejando el balanceo de carga y el enrutamiento de las solicitudes a las instancias de los microservicios.
4. **RabbitMQ**: Implementado para manejar la sincronización entre las bases de datos de los microservicios mediante un sistema de colas y eventos.

---

## Estructura del Proyecto

```
- root_folder/
  - docker-compose.yml       # Orquestación de todos los microservicios
  - File_Server/
    - Dockerfile             # Dockerfile para el servicio de archivos
    - target/
      - app.jar              # Aplicación Java compilada
  - Eureka/
    - Dockerfile             # Dockerfile para el servidor de descubrimiento Eureka
    - target/
      - eureka.jar           # Aplicación Eureka compilada
  - Api_Gateway/
    - Dockerfile             # Dockerfile para el API Gateway
    - target/
      - api-gateway.jar      # Aplicación API Gateway compilada
```

- **`docker-compose.yml`**: Orquesta los microservicios, la base de datos y otros componentes como RabbitMQ.
- **`File_Server/`**: Contiene el código y la configuración del microservicio que gestiona los archivos.
- **`Eureka/`**: Contiene el código y la configuración del servidor de descubrimiento de Eureka.
- **`Api_Gateway/`**: Contiene el código y la configuración del API Gateway.
- **`target/`**: Directorios que contienen los archivos `.jar` compilados para cada servicio.

---

## Tecnologías Aplicadas

- **Java Spring Boot**: Framework principal utilizado para desarrollar los microservicios (File Server, API Gateway, Eureka).
- **PostgreSQL**: Base de datos utilizada para almacenar la información relacionada con los archivos.
- **Docker**: Utilizado para contenerizar todos los microservicios y facilitar su despliegue.
- **Docker Compose**: Orquestación de los microservicios y otros componentes como RabbitMQ y PostgreSQL.
- **RabbitMQ**: Implementado para manejar la sincronización de eventos y comunicación entre las bases de datos.
- **Eureka**: Servidor de descubrimiento de servicios para registrar y monitorear las instancias de microservicios.
- **API Gateway (Zuul o Spring Cloud Gateway)**: Maneja el balanceo de carga y el enrutamiento de las solicitudes hacia los microservicios.

---

## Ejecución del Proyecto

### Requisitos Previos

- **Docker** y **Docker Compose** deben estar instalados en tu máquina.
- **Java 17** para compilar los microservicios.

### Pasos para ejecutar:

1. Clonar el repositorio:
   ```
   git clone https://github.com/tu-usuario/tu-repositorio.git
   cd tu-repositorio
   ```

2. Compilar los microservicios:
   Asegúrate de tener las aplicaciones Java compiladas y los archivos `.jar` en las carpetas `target/`. Si no, puedes compilar los microservicios usando Maven o Gradle:

   ```
   cd File_Server && mvn clean install
   cd ../Eureka && mvn clean install
   cd ../Api_Gateway && mvn clean install
   ```

3. Levantar los contenedores con Docker Compose:
   ```
   docker-compose up --build
   ```

4. Acceder a los servicios:
   - **API Gateway**: [http://localhost:8081](http://localhost:8081)
   - **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
   - **RabbitMQ Dashboard**: [http://localhost:15672](http://localhost:15672) (Usuario: `guest`, Contraseña: `guest`)

### Escalar el servicio de archivos:

Si deseas escalar el servicio `file-service` para que haya múltiples instancias corriendo, puedes hacerlo con el siguiente comando:

```
docker-compose up --scale file-service=3
```

---

## Notas

- El servicio de archivos comparte un volumen donde se almacenan los archivos, mientras que cada instancia tiene su propia base de datos.
- Las instancias se registran automáticamente en **Eureka**, y el **API Gateway** distribuye las solicitudes entre las instancias disponibles usando un sistema de balanceo de carga.

---

## Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo [LICENSE](LICENSE).

---

### ¿Contribuciones?

Si deseas contribuir al proyecto, por favor, realiza un fork y envía un pull request.

---
