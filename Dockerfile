# -------------------------------------------------------------------
# STAGE 1: Build Stage (Compilación)
# Usamos una imagen con Maven y JDK 21 para compilar el proyecto.
# -------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo de configuración de Maven
COPY pom.xml .

# Descargamos las dependencias (Capa de caché para acelerar builds futuros)
# "go-offline" descarga la mayoría de dependencias necesarias
RUN mvn dependency:go-offline -B

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto y generamos el .jar
# -DskipTests: Saltamos tests unitarios en el build de Docker para agilidad
# (se asume que los tests pasaron en el CI/CD pipeline o localmente)
RUN mvn clean package -DskipTests

# -------------------------------------------------------------------
# STAGE 2: Run Stage (Ejecución)
# Usamos una imagen ligera (Alpine) solo con JRE 21 para correr la app.
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Metadatos del mantenedor
LABEL maintainer="Angel Cancho <angelcca2001@gmail.com>"

# Creamos un usuario no-root para seguridad
RUN addgroup -S nexus && adduser -S nexus -G nexus
USER nexus

# Variable de entorno para ubicar el jar
WORKDIR /app

# Copiamos solo el artefacto compilado desde la etapa anterior (STAGE 1)
# El nombre 'nexus-backend-core-0.0.1-SNAPSHOT.jar' debe coincidir con tu pom.xml
# Usamos un wildcard (*) para que funcione sin importar la versión exacta
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto definido en application.properties
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]