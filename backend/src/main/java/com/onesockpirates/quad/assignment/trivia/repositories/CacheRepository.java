package com.onesockpirates.quad.assignment.trivia.repositories;

import org.springframework.stereotype.Component;

import com.onesockpirates.quad.assignment.trivia.managers.IStorageManager;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestionStats;

@Component
public class CacheRepository implements ICacheRepository{
	private IStorageManager<TriviaQuestionStats> database;

	CacheRepository(IStorageManager<TriviaQuestionStats> database){
		this.database = database;
		this.database.intialize("AnswerStats", TriviaQuestionStats.class);
	}

	public void save(TriviaQuestionStats question){
		database.save(question);
	}
    public TriviaQuestionStats findById(String hash){
		return database.query(hash);
	}
    public TriviaQuestionStats updateStats(TriviaQuestionStats stats, Boolean correct){
		TriviaQuestionStats updated = 
			new TriviaQuestionStats(
				stats.getHash(),
				stats.getAnswer(),
				stats.getAsked() + 1, 
				stats.getCorrect() + (correct?1:0)
			);
		database.update(stats.getHash(), updated);
		updated.setAnswer("");
		return updated;

	}
}
