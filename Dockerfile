# Stage 1: Gradle 빌드
FROM gradle:8.11-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

# Stage 2: 실행 환경
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
# 8080 포트 오픈
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
