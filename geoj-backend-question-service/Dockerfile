FROM openjdk:8
VOLUME /tmp
COPY target/*.jar geoj-backend-question-service.jar
ENV PORT=8103
ENTRYPOINT ["java","-jar","-Xms128m","-Xmx256m","geoj-backend-question-service.jar","--spring.profiles.active=prod"]
EXPOSE $PORT
