FROM amazoncorretto:21-alpine
ARG JAR_FILE=build/libs/storefront-app-v2.0.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]