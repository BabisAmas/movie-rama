package com.babisamas.movierama.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public class JwtTestUtil {

    private static final String SECRET_KEY = "Ln2u9LqWbXaQG49T34QeYbZUk6oF5w7RjH3sVc0PzT9k2MjI8wA4mNlP6tVe8bG1";

    public static String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000L);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", String.join(",", roles))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey())
                .compact();
    }

    private static SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}