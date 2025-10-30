FROM sbtscala/scala-sbt:amazoncorretto-al2023-21.0.8_1.11.7_3.3.6 AS build 
ENV NODE_VERSION=20.16.0
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN node --version
RUN npm --version
WORKDIR /app
COPY . /app/
# RUN sbt uiTrivia/fastLinkJS 
RUN cd /app/apps/trivia-fe && npm install && npm run build
RUN mkdir -p /app/src/main/resources/public && cp -r /app/apps/trivia-fe/dist/* /app/src/main/resources/public/
RUN cd /app && sbt compile

FROM amazoncorretto:21-alpine3.21
WORKDIR /app
COPY --from=build /app/target/scala-3.3.6/scala-trivia_3-0.1.0.jar .
ENTRYPOINT ["java","-jar","scala-trivia_3-0.1.0.jar"]