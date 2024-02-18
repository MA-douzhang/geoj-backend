FROM openjdk:8
VOLUME /tmp
COPY target/*.jar geoj-backend-gateway.jar
ENV PORT=8101
ENTRYPOINT ["java","-jar","-Xms32m","-Xmx64m","geoj-backend-gateway.jar","--spring.profiles.active=prod"]
EXPOSE $PORT
