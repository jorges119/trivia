package com.onesockpirates.quad.assignment.trivia.managers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.onesockpirates.quad.assignment.trivia.exceptions.InvalidQuestionException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OpenTriviaException;
import com.onesockpirates.quad.assignment.trivia.helpers.IHasher;
import com.onesockpirates.quad.assignment.trivia.models.OTriviaPayload;
import com.onesockpirates.quad.assignment.trivia.models.OTriviaQuestions;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerRequest;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerResponse;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestion;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestionStats;
import com.onesockpirates.quad.assignment.trivia.repositories.ICacheRepository;

@Component
public class TriviaManager implements ITriviaManager {

	private final ICacheRepository repository;
	private final IHasher hasher;
	private final HttpClient client;
	private final String triviaURL = "opentdb.com";

	@Autowired
	public TriviaManager(ICacheRepository repository, IHasher hasher) {
		this.repository = repository;
		this.hasher = hasher;
		this.client = HttpClient.newBuilder()
			.version(Version.HTTP_2)
			.followRedirects(Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(20))
			.build();
	}

	public TriviaManager(ICacheRepository repository, IHasher hasher, HttpClient client) {
		this.repository = repository;
		this.hasher = hasher;
		this.client = client; 
	}

	private TriviaQuestion prepareQuestion(OTriviaPayload payload) {
		String hash =  hasher.uniqueHash(payload.getQuestion());
		TriviaQuestionStats qs = repository.findById(hash);
		List<String> options = new ArrayList<>(Arrays.asList(payload.getIncorrect_answers()));
		options.add(payload.getCorrect_answer());
		// Let's not forget to randomize, otherwise it is easy to guess again
		Collections.shuffle(options);
		TriviaQuestion q = new TriviaQuestion(
			payload.getQuestion(), 
			hash, 
			options.toArray(String[]::new));
		if (qs == null){
			TriviaQuestionStats nqs = new TriviaQuestionStats(hash, payload.getCorrect_answer(), 0, 0);
			repository.save(nqs);
		}
		return q;
	}

	public TriviaQuestion[] getQuestions(Integer quantity) throws IOException, InterruptedException, OpenTriviaException{
		try{
			UriComponents uri = UriComponentsBuilder.newInstance().scheme("http").host(this.triviaURL).path("api.php").queryParam("amount", quantity).build();
			HttpRequest request = HttpRequest.newBuilder()
			.uri(uri.toUri())
			.timeout(Duration.ofMinutes(2))
			.header("Content-Type", "application/json")
			.GET()
			.build();
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			// TODO Jorge: include session token and manage token refresh
			if (response.statusCode() != 200){
				throw new OpenTriviaException();
			} else {
				OTriviaPayload[] questions = new Gson().fromJson(response.body(), OTriviaQuestions.class).getResults();
				return Stream.of(questions)
							.map(this::prepareQuestion)
							.toArray(TriviaQuestion[]::new);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}

	}

	private TriviaAnswerResponse checkAnswer(TriviaAnswerRequest answer){
		TriviaQuestionStats qs = repository.findById(answer.getHash());
		if (qs == null) {
			return null;
		} else {
			Boolean isCorrect =  qs.getAnswer().equals(answer.getAnswer());
			TriviaQuestionStats uqs = this.repository.updateStats(qs, isCorrect);
			return new TriviaAnswerResponse(uqs.getAsked(), uqs.getCorrect(), isCorrect);
		}
	}

	public TriviaAnswerResponse[] checkQuestion(TriviaAnswerRequest[] answers) throws InvalidQuestionException{
		TriviaAnswerResponse[] checked = 
			Stream.of(answers).map(this::checkAnswer).filter(a -> a!=null).toArray(TriviaAnswerResponse[]::new);
		if (checked.length < answers.length ){
			throw new InvalidQuestionException();
		}
		return checked;
	}
}
