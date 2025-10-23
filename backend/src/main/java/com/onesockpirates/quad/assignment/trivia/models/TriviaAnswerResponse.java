package com.onesockpirates.quad.assignment.trivia.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
public class TriviaAnswerResponse {
	@NonNull private Integer asked;
	@NonNull private Integer correct;
	@NonNull private Boolean success;

}
