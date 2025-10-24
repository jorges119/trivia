package com.onesockpirates.quad.assignment.trivia.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.ranges.RangeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
@Tag(name = "Trivia Controller", description = "APIs for interacting with the Trivia service")
public class TriviaController {

  private final ITriviaManager manager;

  public TriviaController(ITriviaManager manager) {
    this.manager = manager;
  }

  @GetMapping("/test")
  @Operation(summary = "Healthcheck", description = "Check the API is accesible")
  public String index() {
    return "Welcome to the trivia service";
  }

  @CrossOrigin(origins = {"http://localhost:5173","https://trivia.onesockpirates.com"})
  @Operation(summary = "Get Questions", description = "Request an amount of trivia questions")
  @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service could obtain questions",
              content = @Content(schema = @Schema(implementation = TriviaQuestion[].class))),
            @ApiResponse(responseCode = "400", description = "Amount of questions requested out of range (1 to 50)",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Problems with accesing open trivia",
                    content = @Content(schema = @Schema()))
    })
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

  @CrossOrigin(origins = {"http://localhost:5173","https://trivia.onesockpirates.com"})
  @Operation(summary = "POST answers", description = "Request answer checking")
  @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Answers checked succesfully",
              content = @Content(schema = @Schema(implementation = TriviaAnswerResponse[].class))),
            @ApiResponse(responseCode = "404", description = "A question ID was not found",
                    content = @Content(schema = @Schema()))
    })
  @PostMapping("/checkanswers") 
  public ResponseEntity<TriviaAnswerResponse[]> checkAnswer(@RequestBody TriviaAnswerRequest[] answers) throws InvalidQuestionException {
    return new ResponseEntity<>(this.manager.checkQuestion(answers), HttpStatus.CREATED);
  }

}