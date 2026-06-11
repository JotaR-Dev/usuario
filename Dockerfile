FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/usuario-0.0.1-SNAPSHOT.jar  /app/usuario.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/usuario.jar"]