package com.onesockpirates.quad.assignment.trivia;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.onesockpirates.quad.assignment.trivia.exceptions.InvalidQuestionException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OpenTriviaException;
import com.onesockpirates.quad.assignment.trivia.managers.ITriviaManager;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerRequest;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerResponse;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestion;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
class TriviaApplicationTests {

	@LocalServerPort
    int randomServerPort;

	@Autowired
    private TestRestTemplate restTemplate;


	@MockitoBean
	private ITriviaManager manager;

	@AfterEach public void reset_mocks() {
		Mockito.reset(manager);
	}

	@Test
	void contextLoads() {
	}
	
	@Test
	void GET_questions_Returns_OK() throws URISyntaxException, IOException, InterruptedException, OpenTriviaException{
		when(manager.getQuestions(any())).thenReturn(
			new TriviaQuestion[]{
				new TriviaQuestion("q1","h1",new String[]{"a","b","c"})
			});
		final String baseUrl = "http://localhost:"+randomServerPort+"/questions?amount=1";
        URI uri = new URI(baseUrl);
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(uri, String.class);

        assertEquals(200, getResult.getStatusCode().value());
	}
	
	@Test
	void GET_questions_With_Error_Returns_ServerError() throws URISyntaxException, IOException, InterruptedException, OpenTriviaException{
		when(manager.getQuestions(any())).thenThrow(new OpenTriviaException());
		final String baseUrl = "http://localhost:"+randomServerPort+"/questions?amount=1";
        URI uri = new URI(baseUrl);
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(uri, String.class);

        assertEquals(500, getResult.getStatusCode().value());
	}
	
	@Test
	void GET_questions_BeyondAmount_Returns_BadRequest() throws URISyntaxException, IOException, InterruptedException, OpenTriviaException{
		final String baseUrl = "http://localhost:"+randomServerPort+"/questions?amount=51";
        URI uri = new URI(baseUrl);
        ResponseEntity<String> getResult = this.restTemplate.getForEntity(uri, String.class);

        assertEquals(400, getResult.getStatusCode().value());
	}

	@Test
	void POST_Existing_Answers_Returns_CREATED() throws URISyntaxException, InvalidQuestionException{
		when(manager.checkQuestion(any())).thenReturn(new TriviaAnswerResponse[]{
			new TriviaAnswerResponse(0, 0, true)
		});
		final String baseUrl = "http://localhost:"+randomServerPort+"/checkanswers";
        URI uri = new URI(baseUrl);
        TriviaAnswerRequest[] requestData = new TriviaAnswerRequest[]{new TriviaAnswerRequest("=", "a")};
        HttpEntity<TriviaAnswerRequest[]> request = new HttpEntity<>(requestData);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);
        assertEquals(201, result.getStatusCode().value());
	}
	
	@Test
	void POST_Non_Existing_Answers_Returns_NOTFOUND() throws URISyntaxException, InvalidQuestionException{
		when(manager.checkQuestion(any())).thenThrow(new InvalidQuestionException());
		final String baseUrl = "http://localhost:"+randomServerPort+"/checkanswers";
        URI uri = new URI(baseUrl);
        TriviaAnswerRequest[] requestData = new TriviaAnswerRequest[]{new TriviaAnswerRequest("=", "a")};
        HttpEntity<TriviaAnswerRequest[]> request = new HttpEntity<>(requestData);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);
        assertEquals(404, result.getStatusCode().value());
	}
}
