FROM maven:3.9.4-amazoncorretto-17 AS build

WORKDIR /app

#COPY ojdbc11.jar .

COPY . .

#RUN mvn install:install-file -Dfile=ojdbc11.jar -DgroupId=com.oracle -DartifactId=ojdbc11 -Dversion=23.3.0.23.09 -Dpackaging=jar

RUN mvn clean package

FROM amazoncorretto:17

WORKDIR /prod

#COPY ojdbc11.jar .

COPY --from=build /app/ /prod

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "target/lojavirtual-fs-0.0.1-SNAPSHOT.jar"]