# ================================
# Build the application
# ================================
FROM azul-zulu:25 AS builder

WORKDIR /app

# Copy the entire project into the container
COPY . .

# Ensure the Gradle wrapper is executable
RUN chmod +x ./gradlew

# Build the application inside the container
RUN ./gradlew clean bootJar --no-daemon

# ================================
# Run the application
# ================================
FROM azul-zulu:25-jre
WORKDIR /app

# Copy ONLY the built jar from the builder stage
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]