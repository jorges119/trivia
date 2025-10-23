# Quad Solutions Trivia Assingment

This project showcases the use of Spring Framework and React to build a simple Trivia web application that acts as a proxy between open trivia and the user.

## Backend

The aplication exposes two endpoints

* /questions: obtain a set of questions without the answer in the payload
* /answers: check a set of answers and get the status

Refer to the Swagger documentation using the endpoint: /swagger-ui/index.html


## Front-end

The user can select the number of questions and browse through them selecting the appropiate answer. Once 1 or more answers are given the user can choose to finish the game and check all the answers.


## Building and running locally with docker

Both projects are built together in the provided Dockerfile and a single image is created.

```bash
docker build -t trivia .      
docker run -p 8080:8080 trivia
```