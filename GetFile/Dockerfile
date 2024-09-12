# Paso 1: Imagen base con OpenJDK 17
FROM openjdk:17-jdk-alpine

# Paso 2: Instalar PostgreSQL
RUN apk update && apk add postgresql postgresql-contrib

# Paso 3: Set environment variables for PostgreSQL
ENV POSTGRES_DB=your_database
ENV POSTGRES_USER=your_username
ENV POSTGRES_PASSWORD=your_password

# Paso 4: Crear un directorio para tu aplicación
WORKDIR /app

# Paso 5: Copiar el archivo JAR en la imagen
COPY target/GetFile-0.0.1-SNAPSHOT.jar /app/app.jar

# Paso 6: Exponer los puertos para la aplicación y PostgreSQL
EXPOSE 8080 5432

# Paso 7: Crear el directorio donde se almacenarán los archivos
RUN mkdir -p /shared/files

# Paso 8: Agregar un script para inicializar PostgreSQL y ejecutar la aplicación
COPY ./init.sh /app/init.sh
RUN chmod +x /app/init.sh

# Paso 9: Punto de entrada para iniciar ambos servicios
ENTRYPOINT ["/app/init.sh"]
