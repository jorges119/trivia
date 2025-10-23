package com.onesockpirates.quad.assignment.trivia.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    
    public class APIException{
        private String message;
        private Date time;

        public APIException (String message){
            this.message = message;
            this.time = new Date();
        }

        public String getMessage(){
            return this.message;
        }
        public Date getTime(){
            return this.time;
        }
    }

    @ExceptionHandler(value = {InvalidQuestionException.class})
    public ResponseEntity<Object> handleCreateRequestExceptions (InvalidQuestionException e){
        return new ResponseEntity<>(
            new APIException(e.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }
    
    @ExceptionHandler(value = {OpenTriviaException.class})
    public ResponseEntity<Object> handleCreateRequestExceptions (OpenTriviaException e){
        return new ResponseEntity<>(
            new APIException(e.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    @ExceptionHandler(value = {OutOfRangeException.class})
    public ResponseEntity<Object> handleCreateRequestExceptions (OutOfRangeException e){
        return new ResponseEntity<>(
            new APIException(e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

}