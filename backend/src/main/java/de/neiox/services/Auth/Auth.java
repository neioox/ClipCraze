package de.neiox.services.Auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.security.SecureRandom;
import java.util.Base64;

public class Auth {

    String token;
    String secret;

    public String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom(); // Sichere Zufallszahl
        byte[] key = new byte[32]; // 256 bit Schlüssel
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public boolean checkToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                    .withIssuer("neiox")
                    .build()
                    .verify(token);
            System.out.println("Token ist gültig");
            return true;

        } catch (JWTVerificationException exception) {
            // Ungültiges Signatur/Token
            System.out.println("Token ist ungültig");
            return false;
        }
    }

    public void generateToken(){
        try {
            secret = generateSecretKey();
            Algorithm algorithm =  Algorithm.HMAC256(secret);
            String generatedToken = JWT.create()
                    .withIssuer("neiox")
                    .sign(algorithm);
            token = generatedToken;

        } catch (JWTCreationException e){
            // Invalid Signing configuration / Couldn't convert Claims.
            System.out.println(e);
        }
    }

    public String getToken(){
        return token;
    }
}
