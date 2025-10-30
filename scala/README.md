# Scala 3 and Scalajs version of the assingment

Same assingment solved using the awesomeness of Scala


## Structure

There are 3 projects in this solution:

- common: builds models and schemas of this models. It is cross-compiled between sacaljs and scala jvm
- root: Scala3 + ZIO backend
- uiTrivia: ScalaJs + Laminar frontend



## Locally running the project

1. From the /scala folder start the backend ```sbt run```
2. From the /scala folder start the frontend transpilation(live) ```~uiTrivia/fastLinkJS```
3. From the /scala/apps/trivia-fe start the vite application ```npm run dev```


## Build and deploy (WIP)

Although the sbt-native-packager plugin is imported for automatic docker image packaging of the app, a customized Docker file is used to build and package both fe and be together similar to the java solution.

```bash
docker build -t {registry}/trivia:scala .      
```

Run the generated image with 

```bash
docker run -p 8081:8081 {registry}/trivia:scala
```

- The application should be available at http://localhost:8081/index.html
- Swagger documentation available at http://localhost:8081/docs/openapi

The infrastructure is exactly the same as the one used in the java solution.