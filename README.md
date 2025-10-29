# Quad Solutions Trivia Assingment

This project showcases the use of Spring Framework and React to build a simple Trivia web application that acts as a proxy between open trivia and the user.

## Backend

The aplication exposes two endpoints

* /questions: obtain a set of questions without the answer in the payload
* /checkanswers: check a set of answers and get the status

<br />

__Refer to the Swagger documentation using the endpoint:__ https://trivia.onesockpirates.com/swagger-ui/index.html

## Front-end

React application in Typescript, leveraging TanstackQuery
The front-end is directly served by Spring's static file server.

The user can select the number of questions and browse through them selecting the appropiate answer. Once 1 or more answers are given the user can choose to finish the game and check all the answers.

Upon completion statistics on each answer are displayed.

## Demo
Find a live deployment here: 

https://trivia.onesockpirates.com

It will be shutdown at the end of October '25.


## Building and running locally with docker

Both projects are built together in the provided Dockerfile and a single image is created.

```bash
docker build -t {image:tag} .      
docker run -p 8080:8080 {image:tag}
```

Both the backend and front-end can be accessed then in the browser through localhost:8080.

### Pushing image manually to docker
Push your image to Docker Hub, ECR or the repository of your choice:

```bash
docker push {image:tag}
```

## CI/CD

Through Github actions the docker container is being tested, build and pushed to Docker Hub.
The ECS service update is not yet automated.

## Deploying to AWS

Use the included stack.yaml file to deploy the docker image into AWS ECS via cloudformation.

This infrastructure assumes an existing VPC and public subnets in AWS, as well as a SSL certificate in AWS Certificate manager.

DNS records are created manually using Route53 and the alias functionality for A records.

This infrastructure is functional but not necessarily according to functional and security standards (the containers should be running in the private network for instance, autoscaling not configured). Use at your own risk.

Use the console to deploy the stack or alternatively the command line with a command like (not verified, please double check):

```bash
aws cloudformation create-stack \
  --stack-name trivia-stack \
  --template-body file://stack.yaml \
  --parameters \
    ParameterKey=DockerImage,ParameterValue={image:tag} \
    ParameterKey=SubnetIds,ParameterValue={subnet-1,subnet-2} \
    ParameterKey=VpcId,ParameterValue={vpc} \
    ParameterKey=CertificateArn,ParameterValue={arn:aws:acm:*} \
    ParameterKey=DesiredCount,ParameterValue={1|2|...} \
  --capabilities CAPABILITY_IAM
```

## Notes and possible extensions

- Open Trivia token flow not implemented
- No styling or responsivenes added to the Front End. Styled components would be the goto solution for component styling and reuse.
- No unit tests added for the Front End
- No internationalization added (although it would require automated translations...)
- No error pages have been configured or proper error messages are displayed in the front-end other than a generic 'Try Again'
- While the current infrastructure and application fit the description of the assignment serverless solutions are also available and could reduce the operational costs, ie:
  - API Gateway + Lambda + DynamoDB: The current spring application could be packaged as a lambda function. Issues exists with the overhead of initializing de java environment but it ultimately depends on usage expectations and whether the function remains warmed up.
	- API Gateway: abusing the system it woud be possible to setup a proxy endpoint to the open trivia API that through Velocity templates anonymizes the answers. Later the check can be done against the cloudwatch logs (including the original reply from Open Trivia) with a lambda filtering based on the API gateway request identifier. Alternative federation could be used through Cognito to allow the FE directly to parse the logs.
	- AppSync: no code solution with graphql to manage storage and retrieval of questions/answers, api gateway could still be used to access the questions from Open Trivia.
 
## Scala version

I was informed that it would take a while for this repo to be reviewed so I decided to play around and add a 2nd version of the assingment using Scala 3 and ZIO. The front-end will be built in Scalajs.

Run the project with sbt run, the server is configured to run at port 8081. 

Review the Swagger documentation on localhost:{port}/docs/openapi

Check the full scala project README [here](scala/README.md)
