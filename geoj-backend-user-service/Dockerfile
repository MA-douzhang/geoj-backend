FROM openjdk:8
VOLUME /tmp
COPY target/*.jar geoj-backend-user-service.jar
ENV PORT=8102
ENTRYPOINT ["java","-jar","-Xms128m","-Xmx256m","geoj-backend-user-service.jar","--spring.profiles.active=prod"]
EXPOSE $PORT
