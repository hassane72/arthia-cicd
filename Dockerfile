FROM adoptopenjdk:11-jre-hotspot

RUN mkdir uploads

ADD target/*.jar app.jar

EXPOSE 8762
ENTRYPOINT ["java", "-jar", "/app.jar"]