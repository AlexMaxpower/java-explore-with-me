FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar ewm-view.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/ewm-view.jar"]