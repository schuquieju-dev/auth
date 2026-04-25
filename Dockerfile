# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen con Maven para compilar el código
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el archivo de dependencias y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (saltando las pruebas para que sea más rápido)
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Usamos una imagen más ligera de Java solo para correr la app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el .jar generado en la Etapa 1 a esta nueva etapa
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8081

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]