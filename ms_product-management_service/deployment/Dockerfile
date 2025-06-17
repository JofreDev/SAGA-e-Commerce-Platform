FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY *.jar productManagement.jar
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar productManagement.jar" ]
