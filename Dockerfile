# Usamos una imagen de Java (puedes cambiar 17 por 21 si usas esa)
FROM eclipse-temurin:17-jdk-alpine

# Creamos una carpeta para la app
WORKDIR /app

# Copiamos el archivo .jar (asegúrate de que el nombre coincida con el tuyo)
# Tip: Generalmente están en la carpeta target/
COPY target/*.jar app.jar

# Exponemos el puerto (Render usa el 8080 por defecto para Java)
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]