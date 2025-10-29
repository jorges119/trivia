import com.onesockpirates.trivia.RESTServer
import com.onesockpirates.common.models.Question
import Question._
import com.onesockpirates.trivia.endpoints.HealthcheckController
import com.onesockpirates.trivia.endpoints.TriviaController

import zio._
import zio.http._
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.json._
import zio.test._
import com.onesockpirates.common.models.Answer

object DummyQuestionRepository extends Question.Repository {

  override def findById(hash: String): Task[Option[Question]] =
    ZIO.succeed(
      Some(Question("testId", "testQuestion", List("a", "b"), "", 0, 0, false))
    )

  override def save(question: Question): Task[Question] =
    ZIO.succeed(question)

  override def update(question: Question): Task[Option[Question]] =
    ZIO.succeed(Some(question))

  def layer: ZLayer[Any, Throwable, Question.Repository] =
    ZLayer.succeed(DummyQuestionRepository)

}

object TestServerExampleSpec extends ZIOSpecDefault {

  def spec = suite("test http app") {
    test("test hello and fallback routes") {
      for {
        client <- ZIO.service[Client]
        port <- ZIO.serviceWithZIO[Server](_.port)
        testRequest =
          Request
            .get(url = URL.root.port(port))
            .addHeaders(Headers(Header.Accept(MediaType.text.`plain`)))
        _ <- TestServer.addRoutes(
          HealthcheckController.routes ++ TriviaController.routes
        )
        helloResponse <- client(Request.get(testRequest.url / "hello"))
        helloBody <- helloResponse.body.asString
        postResponse <- client(
          Request.post(
            testRequest.url / "checkanswers",
            Body.fromString((List(Answer("hash", "guess"))).toJson)
          )
        )
        getResponse <- client(
          Request.get(testRequest.url / "persons" / "someId")
        )
        postBody <- postResponse.body.asString
        getBody <- getResponse.body.asString
      } yield assertTrue(
        helloResponse.status.code == 200,
        helloBody.contains("Hello"),
        postResponse.status.code == 201,
        postBody.contains("AddedId"),
        getResponse.status.code == 200,
        getBody.fromJson[Question] match
          case Left(value) => false
          case Right(value) =>
            value match
              case Question(id, name, age) => true
      )
    }.provideSome[Client with Driver](
      DummyQuestionRepository.layer,
      TestServer.layer,
      Scope.default
    )
  }.provide(
    ZLayer.succeed(Server.Config.default.onAnyOpenPort),
    Client.default,
    NettyDriver.customized,
    ZLayer.succeed(NettyConfig.defaultWithFastShutdown)
  )

}
