package com.youtube.be.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.youtube.be.dao.UserDao;
import com.youtube.be.dto.AuthResponseDto;
import com.youtube.be.entity.UserEntity;
import com.youtube.be.exception.AuthError;
import com.youtube.be.exception.AuthException;
import com.youtube.be.models.User;
import com.youtube.be.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
//@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String CLIENT_ID = "443093767320-a6j8aaqkbm3eumqj60ahkpa7fl0b4b7a.apps.googleusercontent.com";

    private static final String ACCESS_SECRET = "Z2VuZXJhdGVBdmVyeVNlY3VyZVNlY3JldEtleUhlcmUxMjM=";
    private static final String REFRESH_SECRET = "c2VjdXJlUmVmcm4fnVzaFRva2VuS2V5MTIzNDU2Nzg5MA==";

    private static final long ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000;      // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000; // 7 days

    @Autowired
    private UserDao userDao;


    @Override
    public AuthResponseDto authenticateGoogleUser(String encodedJwt) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new GooglePublicKeysManager.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance()
                    ).build())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken token = verifier.verify(encodedJwt);

            if (token == null) {
                throw new RuntimeException("Invalid token exception");
            }

            GoogleIdToken.Payload payload = token.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String profilePicture = (String) payload.get("picture");

            // Check if user exists in db

            Optional<User> userOptional = userDao.findUserByEmail(email);

            if (userOptional.isEmpty()) {
                // new user
                User userEntityToBeCreated = User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .profilePicture(profilePicture)
                        .build();

                User newUserEntity = userDao.saveUser(userEntityToBeCreated);
                userOptional = Optional.of(newUserEntity);
            }

            User existingUserEntity = userOptional.get();
            String accessToken = generateAccessToken(existingUserEntity.getId());
            String refreshToken = generateRefreshToken(existingUserEntity.getId());

            // Save refresh token in DB

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            throw new AuthException(AuthError.UNAUTHORIZED, "User is unauthorized");
        }
    }

    @Override
    public User authenticateUser(String accessToken) {
        try {
            Claims userClaim = validateAccessToken(accessToken);
            String userId = userClaim.getSubject();
            Optional<User> userOptional = userDao.findUserByUserId(userId);

            if(userOptional.isEmpty()) {
                throw new AuthException(AuthError.USER_NOT_FOUND);
            }

            return userOptional.get();
        } catch (ExpiredJwtException e) {

        } catch (JwtException e) {

        }

        return null;
    }

    private String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getSigningKey(ACCESS_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setId(UUID.randomUUID().toString()) // unique ID per token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(getSigningKey(REFRESH_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateAccessToken(String token) throws JwtException {
        return validateToken(token, getSigningKey(ACCESS_SECRET));
    }

    public static Claims validateRefreshToken(String token) throws JwtException {
        return validateToken(token, getSigningKey(REFRESH_SECRET));
    }

    private static Claims validateToken(String token, Key signingKey) throws JwtException {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build();

        Jws<Claims> jws = parser.parseClaimsJws(token); // throws if invalid or expired
        return jws.getBody();
    }

    private static Key getSigningKey(String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
