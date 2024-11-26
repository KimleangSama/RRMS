FROM openjdk:22-slim
COPY ./target/*.jar app.jar
EXPOSE 8888
CMD ["java", "-jar", "app.jar"]