package com.ayush.blog.security;

import com.ayush.blog.exception.BlogApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

//
//@Component
//public class JwtTokenProvider {
//    @Value("${app.jwt-secret}")
//    private String jwtSecret;
//
//    @Value("${app.jwt-expiration-milliseconds}")
//    private long jwtExpirationDate;
//
//
//    //generate jwt token method
//    public String generateToken(Authentication authentication) {
//        String username = authentication.getName();
//        Date currentDate = new Date();
//        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
//
//        String token = Jwts.builder().setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(expireDate)
//                .signWith(key())
//                .compact();
//        return token;
//    }
//
//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//    }
//
//    // get username from token
////    public String getToken(String token) {
////        return Jwts.parser()
////                .verifyWith((SecretKey) key())
////                .build()
////                .parseSignedClaims(token)
////                .getPayload()
////                .getSubject();
////    }
//
//
//
//    // Validate the token against user details and expiration
//    public Boolean validateToken(String token , UserDetails userDetails) {
//        try{
//            final String username = getToken(token);
//            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//        }catch (MalformedJwtException malformedJwtException) {
//            throw new BlogApiException(HttpStatus.BAD_REQUEST,"Invalid jwt token");
//        } catch (ExpiredJwtException expiredJwtException) {
//            throw  new BlogApiException(HttpStatus.BAD_REQUEST,"expired jwt token");
//        } catch (UnsupportedJwtException unsupportedJwtException) {
//            throw  new BlogApiException(HttpStatus.BAD_REQUEST,"unsupported jwt token");
//        } catch (IllegalArgumentException illegalArgumentException) {
//            throw  new BlogApiException(HttpStatus.BAD_REQUEST,"Jwt claims string is null ");
//        }
//    }
//
//    // new method frm the gfg
//    // Extract the username from the token
//    public String getToken(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from the token
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // Check if the token is expired
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Extract the expiration date from the token
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//}


@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // Generate JWT token method
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (MalformedJwtException malformedJwtException) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "JWT claims string is null");
        }
    }

    // Extract the username from the token
    public String getToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
