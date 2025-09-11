FROM amazoncorretto:21-alpine

RUN apk add --no-cache curl bash
RUN which curl
RUN curl --version

ARG JAR_FILE=build/libs/storefront-app-*.jar
COPY ${JAR_FILE} /app/app.jar
COPY wait-for-keycloak.sh /app/wait-for-keycloak.sh
RUN chmod +x /app/wait-for-keycloak.sh
ENTRYPOINT ["/app/wait-for-keycloak.sh", "keycloak:8181", "java", "-jar", "/app/app.jar"]
