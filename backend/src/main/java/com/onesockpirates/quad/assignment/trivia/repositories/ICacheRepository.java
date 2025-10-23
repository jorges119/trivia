package com.onesockpirates.quad.assignment.trivia.repositories;

import org.springframework.stereotype.Component;

import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestionStats;

@Component
public interface ICacheRepository {

    public void save(TriviaQuestionStats question);
    public TriviaQuestionStats findById(String hash);
    public TriviaQuestionStats updateStats(TriviaQuestionStats stats, Boolean correct);
}