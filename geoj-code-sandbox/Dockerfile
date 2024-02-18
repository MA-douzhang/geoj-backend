FROM openjdk:8
RUN apt update
RUN apt install build-essential gdb -y
VOLUME /tmp
COPY target/*.jar geoj-code-sandbox.jar
ENV PORT=8091
ENTRYPOINT ["java","-jar","-Xms128m","-Xmx256m","geoj-code-sandbox.jar","--spring.profiles.active=prod"]
EXPOSE $PORT
