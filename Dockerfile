FROM gcr.io/distroless/java:11
WORKDIR /app
COPY target/feedbutton-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
