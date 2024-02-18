FROM openjdk:8
VOLUME /tmp
COPY target/*.jar geoj-backend-judge-service.jar
ENV PORT=8104
ENTRYPOINT ["java","-jar","-Xms128m","-Xmx256m","geoj-backend-judge-service.jar","--spring.profiles.active=prod"]
EXPOSE $PORT
