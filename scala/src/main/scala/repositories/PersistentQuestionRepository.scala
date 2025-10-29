package com.onesockpirates.trivia.repositories

import zio.{Random, Task, ZLayer}
import com.onesockpirates.common.models.Question
import zio.ZIO

class PersistentQuestionRepository() extends Question.Repository {

  var memory: List[Question] = List()
  override def save(question: Question): Task[Question] =
    // Not FP
    memory = this.memory :+ question
    ZIO.succeed(question)

  override def findById(id: String): Task[Option[Question]] =
    ZIO.succeed(this.memory.find(q => q.hash == id))

  override def update(question: Question): Task[Option[Question]] =
    val q = this.memory.find(q => q.hash == question.hash)
    if (q.isDefined)
      // Not FP
      val original = q.get
      val newQ = original.copy(
        numGuessed = original.numGuessed + 1,
        numCorrect = (original.numCorrect + (if (question.isCorrect) 1 else 0)),
        isCorrect = question.isCorrect
      )
      memory = newQ +: memory.filter(q => q.hash != question.hash)
      ZIO.succeed(Some(newQ))
    else ZIO.succeed(None)

}

object PersistentQuestionRepository {

  def layer: ZLayer[Any, Throwable, PersistentQuestionRepository] =
    ZLayer.fromZIO(ZIO.succeed(new PersistentQuestionRepository()))
}
