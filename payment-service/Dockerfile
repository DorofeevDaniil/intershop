FROM amazoncorretto:21-alpine
ARG JAR_FILE=build/libs/payment-service-*.jar
COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]