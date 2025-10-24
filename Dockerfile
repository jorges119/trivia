FROM node:20 AS npm-build
WORKDIR /app
COPY trivia-fe/package.json .
RUN npm install
COPY trivia-fe .
RUN npm run build

FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
COPY --from=npm-build /app/dist ./src/main/resources/public
RUN mvn clean package 

FROM amazoncorretto:21-alpine3.21
WORKDIR /app
COPY --from=build /app/target/trivia-0.0.1.jar .
ENTRYPOINT ["java","-jar","trivia-0.0.1.jar"]