############################################
##  Stage 1: Build the JAR (using Maven) ###
############################################
FROM maven:3.9.1-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace

# 1) Copy the entire project (parent + modules)
COPY . .

# 2) Build only the api-gateway module (and its dependencies)
RUN mvn clean package -pl user-service -am -DskipTests

####################################################
##  Stage 2: Run the JAR in a minimal JRE image  ###
####################################################
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 3) Copy the JAR built for api-gateway
COPY --from=builder /workspace/user-service/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# 4) Expose the application port (match your config)
EXPOSE 8085

# 5) Default JVM options
ENV JAVA_OPTS="-Xms128m -Xmx512m"

# 6) Run the application with docker profile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]
