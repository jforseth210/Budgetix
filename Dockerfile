# Make a jar
FROM gradle:8.4-jdk17 as BUILDER

WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY src /app/src

RUN gradle clean build bootJar --no-daemon

# Put the jar in a container
FROM openjdk:17

WORKDIR /app
COPY --from=builder /app/build/libs/bankapp.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

