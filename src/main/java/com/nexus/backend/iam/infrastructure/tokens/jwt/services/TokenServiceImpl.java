package com.nexus.backend.iam.infrastructure.tokens.jwt.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.nexus.backend.iam.infrastructure.tokens.jwt.BearerTokenService;

/**
 * Token service implementation for JWT tokens.
 * This class is responsible for generating and validating JWT tokens.
 * It uses the secret and expiration days from the application.properties file.
 */
@Service
public class TokenServiceImpl implements BearerTokenService {
  private final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

  private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  private static final int TOKEN_BEGIN_INDEX = 7;

  /** HS256 needs a key of at least 256 bits. */
  private static final int MIN_SECRET_BYTES = 32;

  @Value("${authorization.jwt.secret}")
  private String secret;

  @Value("${authorization.jwt.expiration.days}")
  private int expirationDays;

  /**
   * Refuses to start with a weak secret. Otherwise the application would start and fail at the first
   * sign-in, or sign tokens with a key short enough to guess.
   */
  @PostConstruct
  void requireStrongSecret() {
    if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
      throw new IllegalStateException(
          "JWT_SECRET must have at least " + MIN_SECRET_BYTES + " bytes of random text");
    }
  }

  /**
   * This method generates a JWT token from an authentication object.
   *
   * @param authentication the authentication object
   * @return String the JWT token
   * @see Authentication
   */
  @Override
  public String generateToken(Authentication authentication) {
    return buildTokenWithDefaultParameters(authentication.getName());
  }

  /**
   * This method generates a JWT token from a username.
   *
   * @param username the username
   * @return String the JWT token
   */
  public String generateToken(String username) {
    return buildTokenWithDefaultParameters(username);
  }

  /**
   * This method generates a JWT token from a username and a secret.
   * It uses the default expiration days from the application.properties file.
   *
   * @param username the username
   * @return String the JWT token
   */
  private String buildTokenWithDefaultParameters(String username) {
    var issuedAt = new Date();
    var expiration = DateUtils.addDays(issuedAt, expirationDays);
    var key = getSigningKey();
    return Jwts.builder()
        .subject(username)
        .issuedAt(issuedAt)
        .expiration(expiration)
        .signWith(key)
        .compact();
  }

  /**
   * This method extracts the username from a JWT token.
   *
   * @param token the token
   * @return String the username
   */
  @Override
  public String getUsernameFromToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * This method validates a JWT token.
   *
   * @param token the token
   * @return boolean true if the token is valid, false otherwise
   */
  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    }  catch (SignatureException e) {
      logger.error("Invalid JSON Web Token Signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JSON Web Token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JSON Web Token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JSON Web Token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JSON Web Token claims string is empty: {}", e.getMessage());
    } catch (JwtException e) {
      // Any other rejection (e.g. a token signed with HS384/HS512 against a shorter key: WeakKeyException)
      logger.error("Invalid JSON Web Token: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Extract a claim from a token.
   *
   * @param token the token
   * @param claimsResolvers the claims resolver
   * @param <T> the type of the claim
   * @return T the claim
   */
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  /**
   * Extract all claims from a token.
   *
   * @param token the token
   * @return Claims the claims
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Get the signing key.
   *
   * @return SecretKey the signing key
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private boolean isTokenPresentIn(String authorizationParameter) {
    return StringUtils.hasText(authorizationParameter);
  }

  private boolean isBearerTokenIn(String authorizationParameter) {
    return authorizationParameter.startsWith(BEARER_TOKEN_PREFIX);
  }

  private String extractTokenFrom(String authorizationHeaderParameter) {
    return authorizationHeaderParameter.substring(TOKEN_BEGIN_INDEX);
  }

  private String getAuthorizationParameterFrom(HttpServletRequest request) {
    return request.getHeader(AUTHORIZATION_PARAMETER_NAME);
  }

  @Override
  public String getBearerTokenFrom(HttpServletRequest request) {
    String parameter = getAuthorizationParameterFrom(request);
    if (isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) {
      return extractTokenFrom(parameter);
    }
    return null;
  }
}
