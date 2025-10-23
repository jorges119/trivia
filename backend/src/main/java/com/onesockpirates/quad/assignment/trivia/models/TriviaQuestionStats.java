package com.onesockpirates.quad.assignment.trivia.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class TriviaQuestionStats {
	@NonNull private String hash;
	@NonNull private String answer;
	@NonNull private Integer asked;
	@NonNull private Integer correct;
}
