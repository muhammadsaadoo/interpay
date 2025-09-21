############################################
##  Stage 1: Build the JAR (using Maven) ###
############################################
FROM maven:3.9.1-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

# Copy the entire project (parent + modules)
COPY . .

# Build only the eureka-server module (and its dependencies)
RUN mvn clean package -pl eureka-server -am -DskipTests

####################################################
##  Stage 2: Run the JAR in a minimal JRE image  ###
####################################################
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the JAR built for eureka-server
COPY --from=builder /workspace/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose Eureka default port
EXPOSE 8761

# Default JVM options
ENV JAVA_OPTS="-Xms128m -Xmx512m"

# Run Eureka with docker profile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]
