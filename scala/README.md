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
