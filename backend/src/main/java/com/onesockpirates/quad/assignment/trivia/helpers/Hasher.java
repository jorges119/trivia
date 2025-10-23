package com.onesockpirates.quad.assignment.trivia.helpers;

import java.net.InetAddress;
import java.security.*;
import java.time.Instant;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * Class to generate random and unique(highly probable) tokens based on
 * an input String
 */
@Component
public class Hasher implements IHasher{

    private String address;

    /**
     * Constructor
     */
    public Hasher(){
        this.address = InetAddress.getLoopbackAddress().getHostAddress();
    }
    
    /**
     * Generates a token based on an MD5 of the input string and unique machine id and timestamp
     * @param source the input string for which a unique token should be generated
     * @param trimLength the size of the unique token String
     * @throws NoSuchAlgorithmException
     */
    public String uniqueHash(String source, int trimLength) {
        return uniqueHash(source).substring(0, trimLength -1);
    }

    /**
     * Generates a token based on an MD5 of the input string and unique machine id and timestamp
     * @param source the input string for which a unique token should be generated
     * @param trimLength the size of the unique token String
     * @throws NoSuchAlgorithmException
     */
    public String uniqueHash(String source) {
        try{
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(makeSourceUnique(source).getBytes());
            String hash = new String(Base64.getEncoder().encode(m.digest()));
            return hash.replace("/","b");
        } catch (NoSuchAlgorithmException e){
            // Can't happen
            return "error";
        }
    }

    private String makeSourceUnique(String source){
        return source + this.address + Instant.now().toEpochMilli();
    }
}
