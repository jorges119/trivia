package com.onesockpirates.quad.assignment.trivia.exceptions;

public class InvalidQuestionException extends Exception {
	public InvalidQuestionException() {
        super("Question does not exist");
    }
}
