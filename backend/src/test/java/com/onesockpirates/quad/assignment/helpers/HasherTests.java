package com.onesockpirates.quad.assignment.helpers;

import org.junit.jupiter.api.Test;

import com.onesockpirates.quad.assignment.trivia.helpers.Hasher;

import static org.junit.jupiter.api.Assertions.*;

class HasherTests {

    private final Hasher fixture = new Hasher();
    
    @Test
    void Hasher_WithStringInput_GeneratesHash() throws Exception{
       String hash = fixture.uniqueHash("myString"); 
       assertEquals(24, hash.length());
    }

    @Test
    void Hasher_WithEmptyStringInput_GeneratesHash() throws Exception{
       String hash = fixture.uniqueHash(""); 
       assertEquals(24, hash.length());
    }

    @Test
    void Hasher_WithURLStringInput_GeneratesHash() throws Exception{
       String hash = fixture.uniqueHash("https://www.google.com/search?q=test+me&oq=test+me&aqs=chrome..69i57j0l4j69i60l3.1985j0j4&sourceid=chrome&ie=UTF-8"); 
       assertEquals(24, hash.length());
    }

    @Test
    void Hasher_WithSameStringInput_GeneratesDifferntHash() throws Exception{
       String hash1 = fixture.uniqueHash("myString"); 
       Thread.sleep(1);
       String hash2 = fixture.uniqueHash("myString"); 
       assertNotEquals(hash1, hash2);
    }
}

