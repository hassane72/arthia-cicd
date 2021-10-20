FROM adoptopenjdk:11-jre-hotspot

RUN apt update
RUN apt install -y ffmpeg

RUN mkdir uploads

ADD target/*.jar app.jar

EXPOSE 8072
ENTRYPOINT ["java", "-jar", "/app.jar"]