FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /application
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw clean package -DskipTests
RUN java -Djarmode=layertools -jar ./target/*.jar extract

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /application
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.JarLauncher"]

EXPOSE 8080
