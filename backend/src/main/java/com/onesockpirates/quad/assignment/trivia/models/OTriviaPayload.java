package com.onesockpirates.quad.assignment.trivia.models;

import lombok.Data;

@Data
public class OTriviaPayload {
	private String type;
	private String difficulty;
	private String category;
	private String question;
	private String correct_answer;
	private String[ ] incorrect_answers ;

}
