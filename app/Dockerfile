FROM docker.paintbot.cygni.se:5000/java8:latest
MAINTAINER emil.breding@bisnode.com

EXPOSE 8080

COPY build/libs/paintbot-app-*.jar /paintbot-app.jar

CMD java -jar -Xmx4096m -Dspring.profiles.active=production /paintbot-app.jar
