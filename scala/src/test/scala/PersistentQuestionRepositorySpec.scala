package com.onesockpirates.trivia.repositories

import com.onesockpirates.common.models.Question
import zio._
import zio.test._

object PersistentQuestionRepositorySpec extends ZIOSpecDefault {

  def spec = suite("PersistentQuestionRepository")(
    test("should save a question successfully") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        question = Question("test-id", "What is 2+2?", List("3", "4", "5"), "4", 0, 0, false)
        saved <- repo.save(question)
      } yield assertTrue(
        saved == question
      )
    },

    test("should find a question by id after saving") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        question = Question("find-test-id", "What is the capital of France?", List("London", "Paris", "Berlin"), "Paris", 0, 0, false)
        _ <- repo.save(question)
        found <- repo.findById("find-test-id")
      } yield assertTrue(
        found.isDefined,
        found.get == question
      )
    },

    test("should return None when finding a non-existent question") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        found <- repo.findById("non-existent-id")
      } yield assertTrue(
        found.isEmpty
      )
    },

    test("should update an existing question correctly") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        originalQuestion = Question("update-test-id", "What is 3+3?", List("5", "6", "7"), "6", 0, 0, false)
        _ <- repo.save(originalQuestion)
        questionWithAnswer = originalQuestion.copy(isCorrect = true)
        updated <- repo.update(questionWithAnswer)
      } yield assertTrue(
        updated.isDefined,
        updated.get.hash == "update-test-id",
        updated.get.numGuessed == 1,
        updated.get.numCorrect == 1,
        updated.get.isCorrect == true
      )
    },

    test("should update question with incorrect answer") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        originalQuestion = Question("incorrect-test-id", "What is 4+4?", List("7", "8", "9"), "8", 0, 0, false)
        _ <- repo.save(originalQuestion)
        questionWithWrongAnswer = originalQuestion.copy(isCorrect = false)
        updated <- repo.update(questionWithWrongAnswer)
      } yield assertTrue(
        updated.isDefined,
        updated.get.hash == "incorrect-test-id",
        updated.get.numGuessed == 1,
        updated.get.numCorrect == 0,
        updated.get.isCorrect == false
      )
    },

    test("should handle multiple updates correctly") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        originalQuestion = Question("multiple-update-id", "What is 5+5?", List("9", "10", "11"), "10", 0, 0, false)
        _ <- repo.save(originalQuestion)
        // First update - correct answer
        firstUpdate = originalQuestion.copy(isCorrect = true)
        _ <- repo.update(firstUpdate)
        // Second update - incorrect answer
        secondUpdate = originalQuestion.copy(isCorrect = false)
        finalResult <- repo.update(secondUpdate)
      } yield assertTrue(
        finalResult.isDefined,
        finalResult.get.numGuessed == 2,
        finalResult.get.numCorrect == 1,
        finalResult.get.isCorrect == false
      )
    },

    test("should return None when updating a non-existent question") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        nonExistentQuestion = Question("does-not-exist", "What is 6+6?", List("11", "12", "13"), "12", 0, 0, false)
        updated <- repo.update(nonExistentQuestion)
      } yield assertTrue(
        updated.isEmpty
      )
    },

    test("should maintain multiple questions in memory") {
      for {
        repo <- ZIO.service[PersistentQuestionRepository]
        question1 = Question("multi-1", "Question 1?", List("a", "b", "c"), "a", 0, 0, false)
        question2 = Question("multi-2", "Question 2?", List("x", "y", "z"), "y", 0, 0, false)
        _ <- repo.save(question1)
        _ <- repo.save(question2)
        found1 <- repo.findById("multi-1")
        found2 <- repo.findById("multi-2")
      } yield assertTrue(
        found1.isDefined,
        found2.isDefined,
        found1.get == question1,
        found2.get == question2
      )
    }
  ).provide(
    ZLayer.fromZIO(ZIO.succeed(new PersistentQuestionRepository()))
  )
}