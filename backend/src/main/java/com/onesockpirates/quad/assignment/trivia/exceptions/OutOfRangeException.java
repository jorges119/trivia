package com.onesockpirates.quad.assignment.trivia.exceptions;

public class OutOfRangeException extends Exception {
	public OutOfRangeException(String name, Integer min, Integer max) {
        super(String.format("Value of %s is out of of range (%d - %d).", name, min, max));
    }
}
