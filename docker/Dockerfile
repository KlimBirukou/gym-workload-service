FROM amazoncorretto:21-al2023

WORKDIR /app

COPY build/libs/gym-workload-server-*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
