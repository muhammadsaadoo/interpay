############################################
##  Stage 1: Build the JAR (using Maven) ###
############################################
FROM maven:3.9.1-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

# 1) Copy just the pom.xml to grab dependencies
COPY pom.xml .

# 2) Download all dependencies to cache (faster rebuilds)
RUN mvn dependency:go-offline -B

# 3) Copy the rest of the application's source code
COPY src ./src

# 4) Package everything into a fat JAR
RUN mvn clean package -DskipTests

####################################################
##  Stage 2: Run the JAR in a minimal JRE image  ###
####################################################
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 5) Copy the "built" JAR from the builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# 6) Expose port 8080 (must match server.port in application-dev.yaml)
EXPOSE 8080

# 7) Set a default memory allocation (tweak as needed)
ENV JAVA_OPTS="-Xms128m -Xmx512m"

# 8) Launch the JAR, activating the "docker" profile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]