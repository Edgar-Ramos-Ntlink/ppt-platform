FROM openjdk:11-jre
VOLUME /tmp

RUN useradd -d /home/appuser -m -s /bin/bash appuser
USER appuser

HEALTHCHECK --interval=5m --timeout=3s CMD curl -f http://localhost:8443/actuator/health/ || exit 1

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY keystore.p12 keystore.p12
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev","-jar","/app.jar"]