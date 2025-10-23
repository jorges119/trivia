package com.onesockpirates.quad.assignment.trivia.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class TriviaQuestion {
	@NonNull private String question;
	@NonNull private String hash;
	@NonNull private String[] options;
	
}
