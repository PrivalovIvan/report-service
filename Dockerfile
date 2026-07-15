FROM node:18-alpine AS frontend-builder

WORKDIR /app/frontend

COPY frontend/package*.json ./

RUN npm install

COPY frontend/ .

RUN npm run build

FROM maven:3.9-eclipse-temurin-21 AS backend-builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src/ src/

COPY --from=frontend-builder /app/frontend/build /app/src/main/resources/static

COPY test-data.db3 .

RUN mvn clean package -DskipTests

# -------- Этап 3: Финальный образ (JRE) --------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=backend-builder /app/target/report-service-0.0.1-SNAPSHOT.jar app.jar
COPY --from=backend-builder /app/test-data.db3 test-data.db3

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
