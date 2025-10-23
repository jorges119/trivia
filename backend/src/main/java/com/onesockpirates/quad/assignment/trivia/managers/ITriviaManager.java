package com.onesockpirates.quad.assignment.trivia.managers;

import java.io.IOException;

import com.onesockpirates.quad.assignment.trivia.exceptions.InvalidQuestionException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OpenTriviaException;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerRequest;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerResponse;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestion;

public interface ITriviaManager {
	public TriviaQuestion[] getQuestions(Integer quantity) throws IOException, InterruptedException, OpenTriviaException;
	public TriviaAnswerResponse[] checkQuestion(TriviaAnswerRequest[] answers) throws InvalidQuestionException;
}