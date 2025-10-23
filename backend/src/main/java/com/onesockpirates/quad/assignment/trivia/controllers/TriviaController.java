package com.onesockpirates.quad.assignment.trivia.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.ranges.RangeException;

import com.onesockpirates.quad.assignment.trivia.exceptions.InvalidQuestionException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OpenTriviaException;
import com.onesockpirates.quad.assignment.trivia.exceptions.OutOfRangeException;
import com.onesockpirates.quad.assignment.trivia.managers.ITriviaManager;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerRequest;
import com.onesockpirates.quad.assignment.trivia.models.TriviaAnswerResponse;
import com.onesockpirates.quad.assignment.trivia.models.TriviaQuestion;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@RestController
public class TriviaController {

  private final ITriviaManager manager;

  public TriviaController(ITriviaManager manager) {
    this.manager = manager;
  }

  @GetMapping("/test")
  public String index() {
    return "Welcome to the trivia service";
  }

  @CrossOrigin(origins = "http://localhost:5173")
  @GetMapping("/questions")
  public ResponseEntity<TriviaQuestion[]> getQuestions(@RequestParam Integer amount) 
    throws 
      IOException, 
      InterruptedException, 
      OpenTriviaException, 
      OutOfRangeException
  {
    if (amount < 0 || amount > 50){
      throw new OutOfRangeException("amount", 0,50);
    }
    try{
      TriviaQuestion[] t = this.manager.getQuestions(amount);
      return new ResponseEntity<>(t, HttpStatus.OK);
    } catch (Exception e){
      System.out.println(e.getMessage());
    }
    return new ResponseEntity<>(this.manager.getQuestions(amount), HttpStatus.OK);
  }

  @CrossOrigin(origins = "http://localhost:5173")
  @PostMapping("/answers") 
  public ResponseEntity<TriviaAnswerResponse[]> checkAnswer(@RequestBody TriviaAnswerRequest[] answers) throws InvalidQuestionException {
    return new ResponseEntity<>(this.manager.checkQuestion(answers), HttpStatus.CREATED);
  }

}