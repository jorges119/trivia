package com.onesockpirates.quad.assignment.managers;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.onesockpirates.quad.assignment.trivia.exceptions.InvalidQuestionException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OpenTriviaException;
import com.onesockpirates.quad.assignment.trivia.helpers.IHasher;
import com.onesockpirates.quad.assignment.trivia.managers.TriviaManager;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerRequest;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerResponse;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestion;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestionStats;
import com.onesockpirates.quad.assignment.trivia.repositories.ICacheRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import org.junit.jupiter.api.BeforeEach;

class TriviaManagerTests {

    private TriviaManager fixture;
    private ICacheRepository repository;
    private IHasher hasher;
    private HttpClient client;

    @BeforeEach
    void setup(){
        repository = mock(ICacheRepository.class);
        hasher = mock(IHasher.class);
        client = mock(HttpClient.class);
        fixture = new TriviaManager(repository, hasher, client);
    }


    @Test
    void GetQuestionsCorrectlyReturnsQuestions() throws Exception{
        String expectedResponse = "{\"response_code\":0,\"results\":[{\"type\":\"boolean\",\"difficulty\":\"easy\",\"category\":\"Entertainment: Video Games\",\"question\":\"q1\",\"correct_answer\":\"False\",\"incorrect_answers\":[\"True\"]},{\"type\":\"multiple\",\"difficulty\":\"easy\",\"category\":\"Entertainment: Television\",\"question\":\"q2\",\"correct_answer\":\"Magnitude\",\"incorrect_answers\":[\"Star Burns\",\"Leonard\",\"Senoir Chang\"]}]}";
        HttpResponse<String> httpResponse = Mockito.mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(expectedResponse);

        when(
            client.send(any(HttpRequest.class), 
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())
        ).thenReturn(httpResponse);
        when(hasher.uniqueHash(anyString())).thenReturn("xxxxxxx");
        TriviaQuestion[] result = fixture.getQuestions(2);
        assertEquals(2, result.length);
        assertEquals("q1", result[0].getQuestion());
        assertEquals("q2", result[1].getQuestion());
    }
    
    @Test
    void GetQuestionsThrowsOnOpenTriviaError() throws Exception{
        String expectedResponse = "{\"response_code\":0,\"results\":[{\"type\":\"boolean\",\"difficulty\":\"easy\",\"category\":\"Entertainment: Video Games\",\"question\":\"q1\",\"correct_answer\":\"False\",\"incorrect_answers\":[\"True\"]},{\"type\":\"multiple\",\"difficulty\":\"easy\",\"category\":\"Entertainment: Television\",\"question\":\"q2\",\"correct_answer\":\"Magnitude\",\"incorrect_answers\":[\"Star Burns\",\"Leonard\",\"Senoir Chang\"]}]}";
        HttpResponse<String> httpResponse = Mockito.mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn(expectedResponse);

        when(
            client.send(any(HttpRequest.class), 
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())
        ).thenReturn(httpResponse);
        assertThrows(OpenTriviaException.class, () -> {
            fixture.getQuestions(2);
        });
    }
    
    @Test
    void CheckInvalidQuestionThrowsException() throws Exception{
        when(repository.findById(any())).thenReturn(null);
        assertThrows(InvalidQuestionException.class, () -> {
            fixture.checkQuestion(new TriviaAnswerRequest[]{
                new TriviaAnswerRequest("hhhh", "qqq")
            });
        });
    }
    @Test
    void CorrectlyChecksCorrectAnswerAgainstRepo() throws Exception{
        when(repository.findById(any())).thenReturn(new TriviaQuestionStats("hhh", "a1", 0, 0));
        when(repository.updateStats(any(), any())).thenReturn(new TriviaQuestionStats("hhh", "a1", 1, 1));
        TriviaAnswerResponse[] result = fixture.checkQuestion(new TriviaAnswerRequest[]{
            new TriviaAnswerRequest("hhh", "a1")
        });
        assertTrue(result[0].getSuccess());
    }
    
    @Test
    void CorrectlyChecksIncorrectAnswerAgainstRepo() throws Exception{
        when(repository.findById(any())).thenReturn(new TriviaQuestionStats("hhh", "ax", 0, 0));
        when(repository.updateStats(any(), any())).thenReturn(new TriviaQuestionStats("hhh", "a1", 1, 1));
        TriviaAnswerResponse[] result = fixture.checkQuestion(new TriviaAnswerRequest[]{
            new TriviaAnswerRequest("hhh", "a1")
        });
        assertFalse(result[0].getSuccess());
    }
    
    @Test
    void CorrectlyHandlesMultipleChecks() throws Exception{
        when(repository.findById("hhh1")).thenReturn(new TriviaQuestionStats("hhh", "a1", 0, 0));
        when(repository.findById("hhh2")).thenReturn(new TriviaQuestionStats("hhh", "a2", 0, 0));
        when(repository.updateStats(any(), any())).thenReturn(new TriviaQuestionStats("hhh", "a1", 1, 1));
        TriviaAnswerResponse[] result = fixture.checkQuestion(new TriviaAnswerRequest[]{
            new TriviaAnswerRequest("hhh1", "a1"),
            new TriviaAnswerRequest("hhh2", "a2")
        });
        assertTrue(result[0].getSuccess());
        assertTrue(result[1].getSuccess());
    }
}
