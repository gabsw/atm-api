#### Stage 1: Build the application
FROM maven:3.6.3-openjdk-17-slim as build

# Set the current working directory inside the image
WORKDIR /app

# Copy project files to the image
COPY mvnw  pom.xml ./
#COPY .mvn .mvn
COPY src src/

# Package the application
RUN mvn package -DskipTests && mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#### Stage 2: A minimal docker image with command to run the app
FROM openjdk:17.0.2-jdk-slim

ARG DEPENDENCY=/app/target/dependency

# Copy project dependencies from the build stage
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Expose port 80 to the Docker host, so we can access it
# from the outside.
EXPOSE 80

ENTRYPOINT ["java","-cp","app:app/lib/*","com.zinkworks.challenge.atm.machine.Application"]