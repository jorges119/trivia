package com.onesockpirates.quad.assignment.trivia.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class TriviaAnswerRequest {
	@NonNull private String hash;
	@NonNull private String answer;
}
