import com.onesockpirates.trivia.RESTServer
import com.onesockpirates.common.models.Question
import Question._
import com.onesockpirates.trivia.routes.HealthcheckController
import com.onesockpirates.trivia.routes.TriviaController

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

object DummyPullerService
    extends com.onesockpirates.trivia.services.PullerService {
  import com.onesockpirates.common.models.OTDBQuestion

  override def getquestions(amount: Int): Task[List[OTDBQuestion]] =
    ZIO.succeed(
      List(OTDBQuestion("a", "b", "c", "question1", "bla", List("blo", "bli")))
    )

  def layer: ZLayer[
    Any,
    Throwable,
    com.onesockpirates.trivia.services.PullerService
  ] =
    ZLayer.succeed(DummyPullerService)
}

object RESTServiceSpec extends ZIOSpecDefault {

  def spec = suite("RESTServiceSpec") {
    test("should serve correct routes") {
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
        getResponse <- client(
          Request.get(testRequest.url / "questions?amount=10")
        )
        getBody <- getResponse.body.asString
        postResponse <- client(
          Request.post(
            testRequest.url / "checkanswers",
            Body.fromString("""[{"hash": "hash", "guess": "guess"}]""")
          )
        )
        postBody <- postResponse.body.asString
      } yield assertTrue(
        getResponse.status.code == 200,
        getBody.contains("question1"),
        postResponse.status.code == 201,
        postBody.contains("testId")
      )
    }.provideSome[Client with Driver](
      DummyQuestionRepository.layer,
      DummyPullerService.layer,
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
