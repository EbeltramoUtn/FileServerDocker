#!/bin/sh

# Crear el directorio para los archivos de lock si no existe
if [ ! -d "/run/postgresql" ]; then
  mkdir -p /run/postgresql
  chown postgres:postgres /run/postgresql
  chmod 775 /run/postgresql
fi

# Inicializar la base de datos si es la primera vez
if [ ! -d "/var/lib/postgresql/data" ]; then
  echo "Inicializando base de datos de PostgreSQL..."
  su postgres -c "initdb -D /var/lib/postgresql/data"
fi

# Iniciar PostgreSQL
su postgres -c "pg_ctl start -D /var/lib/postgresql/data"

# Esperar hasta que PostgreSQL esté listo
until pg_isready -U postgres; do
  echo "Esperando a PostgreSQL..."
  sleep 2
done

# Crear el usuario si no existe
echo "Verificando si el usuario $POSTGRES_USER existe..."
psql -U postgres -tc "SELECT 1 FROM pg_roles WHERE rolname = '$POSTGRES_USER'" | grep -q 1 || psql -U postgres -c "CREATE ROLE $POSTGRES_USER WITH LOGIN SUPERUSER PASSWORD '$POSTGRES_PASSWORD';"

# Crear la base de datos si no existe
echo "Verificando si la base de datos $POSTGRES_DB existe..."
psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = '$POSTGRES_DB'" | grep -q 1 || psql -U postgres -c "CREATE DATABASE $POSTGRES_DB OWNER $POSTGRES_USER;"


# Ejecutar la aplicación Spring Boot
java -jar /app/app.jar
