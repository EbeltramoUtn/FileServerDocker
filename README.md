
# Proyecto de Microservicios - FileServer

## Propósito

Este proyecto tiene como objetivo implementar una arquitectura de microservicios utilizando **Java Spring Boot** para gestionar archivos. Cada microservicio tiene su propia base de datos, pero comparten un volumen común para el almacenamiento de archivos. Los servicios están orquestados utilizando **Docker Compose**, y el proyecto incluye componentes adicionales para la sincronización, descubrimiento de servicios, y balanceo de carga.

El sistema está diseñado para escalar fácilmente y permite la integración con **Prometheus** para monitoreo, junto con **Grafana** para la visualización de métricas.

---

## Estructura del Proyecto

```
- root_folder/
  - docker-compose.yml       # Orquestación de todos los microservicios
  - ApiGateway/              # Contiene el código del API Gateway
    - Dockerfile             # Dockerfile para la contenerización del ApiGateway
    - target/                # Aplicación Java compilada
  - Eureka/                  # Código y configuración del servidor Eureka
    - Dockerfile             
    - target/                
  - SaveFile/                # Microservicio para guardar archivos
    - Dockerfile             
    - target/                
  - GetFile/                 # Microservicio para obtener archivos
    - Dockerfile             
    - target/                
  - Verifier/                # Microservicio que verifica los archivos al inicio
    - Dockerfile             
    - target/
```

---

## Componentes Clave

1. **ApiGateway**:
   - Función: Enruta las solicitudes hacia los microservicios correspondientes y gestiona el balanceo de carga.
   - Tecnologías: Spring Cloud Gateway, Eureka, Resilience4j (para circuit breakers).
   - Fallbacks: Usa `FallbackController` para manejar el fallo de servicios.

2. **Eureka Server**:
   - Función: Servidor de descubrimiento de servicios que permite la autodetección de los microservicios.
   - Tecnologías: Spring Cloud Netflix Eureka.

3. **SaveFile Microservice**:
   - Función: Gestiona la carga y almacenamiento de archivos en un volumen compartido. Publica eventos en RabbitMQ para sincronizar los datos entre las instancias.
   - **Base de Datos**: Cada instancia de `SaveFile` tiene su propia base de datos PostgreSQL dentro de su contenedor Docker.
   - Tecnologías: Spring Boot, PostgreSQL, RabbitMQ.

4. **GetFile Microservice**:
   - Función: Permite la descarga de archivos previamente cargados. Obtiene la información de una base de datos PostgreSQL dentro de su contenedor Docker, sincronizada con el microservicio `SaveFile`.
   - **Base de Datos**: Al igual que `SaveFile`, cada instancia de `GetFile` tiene su propia base de datos PostgreSQL que se gestiona dentro de su contenedor Docker.
   - Tecnologías: Spring Boot, PostgreSQL, RabbitMQ.

5. **Verifier Microservice**:
   - Función: Al iniciar, verifica los archivos en el volumen compartido y los compara con la base de datos, enviando eventos para sincronizar el estado de los archivos.
   - Tecnologías: Spring Boot, RabbitMQ.
   - No es una API: Verifier no expone endpoints HTTP. Se ejecuta como una aplicación de línea de comandos al arrancar, realizando la verificación automáticamente.

6. **Prometheus y Grafana**:
   - Función: Monitorización del sistema, visualización de métricas y rendimiento.
   - Tecnologías: Micrometer, Prometheus, Grafana.

---

## Configuración de RabbitMQ

El sistema utiliza **RabbitMQ** como sistema de mensajería para sincronizar los datos entre los microservicios. Todos los microservicios están conectados a un **Fanout Exchange** llamado `file_fanout_exchange`. Este tipo de exchange permite que cualquier mensaje publicado en él se reenvíe a todas las colas asociadas, asegurando que cada microservicio reciba la información de manera simultánea.

### Funcionamiento General

1. **Fanout Exchange**: Los microservicios `SaveFile`, `GetFile`, y `Verifier` están registrados al **Fanout Exchange** (`file_fanout_exchange`). Cada microservicio tiene una cola duradera propia, que está vinculada a este exchange.
  
2. **Publicación de Mensajes**:
   - Cuando un archivo se guarda en el microservicio `SaveFile`, se publica un mensaje con la información del archivo (nombre, hash, tipo MIME, etc.) en el **Fanout Exchange**.
   - Todas las instancias de los microservicios `SaveFile` y `GetFile` reciben este mensaje, incluso la instancia que emitió el mensaje. Cada una guarda los datos del archivo en su propia base de datos.

3. **Microservicio Verifier**:
   - Al iniciar, el microservicio `Verifier` escanea el volumen compartido en busca de archivos y publica mensajes con la información de cada archivo encontrado en el mismo **Fanout Exchange**.
   - Todos los microservicios que están conectados al exchange recibirán estos mensajes y actualizarán sus bases de datos en consecuencia.

### Resumen

- **Fanout Exchange**: `file_fanout_exchange`
- **Colas**: Cada microservicio tiene su propia cola duradera vinculada al exchange.
- **Publicaciones**:
  - `SaveFile`: Publica información del archivo guardado.
  - `Verifier`: Publica información de los archivos encontrados en el volumen compartido.
- **Mensajes Recibidos**: Todos los microservicios conectados reciben los mensajes del exchange, incluso la instancia que los envió, asegurando que cada microservicio mantenga su base de datos sincronizada.

---

## Bases de Datos

### PostgreSQL dentro de los contenedores de `GetFile` y `SaveFile`

Cada microservicio `GetFile` y `SaveFile` contiene su propia instancia de **PostgreSQL** dentro de su contenedor Docker. Estos microservicios gestionan bases de datos independientes, pero sincronizan la información a través de **RabbitMQ**.

---

## Endpoints de los Microservicios

### 1. **GetFile Microservice**

- **GET /getfile/{uuid}**
  - Descripción: Recupera los detalles del archivo utilizando su UUID.
  - Parámetros:
    - `uuid` (PathVariable): El identificador único del archivo.
  - Respuesta:
    - **200 OK**: Devuelve los metadatos del archivo (UUID, hash, nombre, mimeType, extensión, y bytes del archivo).
    - **404 Not Found**: Si no se encuentra el archivo.
    - **500 Internal Server Error**: Error en el servidor.

#### Ejemplo de Respuesta:
```json
{
  "uuid": "f38b7319-6bd1-482e-a7b4-c531ebc0fc43",
  "hashMd5": "d41d8cd98f00b204e9800998ecf8427e",
  "fileName": "example.txt",
  "mimeType": "text/plain",
  "extension": "txt",
  "bytes": "SGVsbG8gd29ybGQ="  // Base64 encoded content
}
```
- **GET /getfile/ping**
  - Descripción: Verifica la conectividad y el estado del microservicio.
  - Respuesta: `pong`
---

### 2. **SaveFile Microservice**

- **POST /savefile/upload**
  - Descripción: Sube un archivo y, opcionalmente, verifica su integridad mediante hashes MD5 o SHA-256.
  - Parámetros:
    - `file` (MultipartFile): El archivo que se va a cargar.
    - `hashMd5` (opcional): Hash MD5 para verificar la integridad del archivo.
    - `hashSha256` (opcional): Hash SHA-256 para verificar la integridad del archivo.
  - Respuesta:
    - **200 OK**: Devuelve el UUID del archivo subido.
    - **400 Bad Request**: Si hay un problema con la solicitud o el archivo.
    - **500 Internal Server Error**: Error en el servidor.

#### Ejemplo de Respuesta:
```json
"f38b7319-6bd1-482e-a7b4-c531ebc0fc43"
```
- **GET /savefile/ping**
  - Descripción: Verifica la conectividad y el estado del microservicio.
  - Respuesta: `pong`
---


## Instalación y Ejecución

### Requisitos

- Docker instalado.
- Java 17 para compilar los microservicios.

### Pasos para Ejecutar

1. Clona el repositorio:
   ```bash
   git clone https://github.com/EbeltramoUtn/FileServerDocker
   cd FileServerDocker
   ```

2. Compila los microservicios:
   ```bash
   cd ApiGateway && mvn package -DskipTests
   cd ../Eureka && mvn package -DskipTests
   cd ../SaveFile && mvn package -DskipTests
   cd ../GetFile && mvn package -DskipTests
   cd ../Verifier && mvn package -DskipTests
   ```

3. Levanta los contenedores con Docker Compose:
   ```bash
   docker-compose up --build -d
   ```
### Escalar el servicio de archivos:

Si deseas escalar el servicio `FileServerGet` para que haya múltiples instancias corriendo, puedes hacerlo con el siguiente comando:

```
docker-compose up --scale file-server-get=3
```

4. Accede a los servicios:
   - **ApiGateway**: [http://localhost:8080](http://localhost:8080)
   - **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
   - **RabbitMQ Dashboard**: [http://localhost:15672](http://localhost:15672) (usuario: `guest`, contraseña: `guest`)
   - **Grafana**: [http://localhost:3000](http://localhost:3000) (usuario: `admin`, contraseña: `admin`)

---

## Tareas pendientes

- Implementar los tests unitarios y de integración para todos los microservicios.
- Revisar y mejorar el manejo de errores en los microservicios.
- Optimizar la configuración de Prometheus y Grafana para obtener métricas más detalladas.
- Completar la documentación detallada de cada servicio, incluyendo casos de uso y ejemplos.

---

### ¿Contribuciones?

Si deseas contribuir al proyecto, por favor, realiza un fork y envía un pull request.

---