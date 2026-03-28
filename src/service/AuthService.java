package service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import model.Claims;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton service handling JWT-based authentication and BCrypt password
 * hashing. Issues signed tokens on login containing role, orgId, and
 * enterpriseId claims. All panels validate the token before rendering.
 *
 * JWT library:  io.jsonwebtoken (jjwt) 0.11.5
 * Hash library: org.mindrot.jbcrypt 0.4
 *
 * Usage:
 *   AuthService auth = AuthService.getInstance();
 *
 *   // Hash a password before storing
 *   String hash = auth.hashPassword("plaintext");
 *
 *   // Verify on login
 *   boolean ok = auth.verifyPassword("plaintext", hash);
 *
 *   // Issue token after successful login
 *   String token = auth.issueJWT(userId, role, orgId, enterpriseId, email);
 *
 *   // Validate on every panel transition
 *   Claims claims = auth.validateJWT(token);
 *   if (claims != null && claims.isValid()) { ... }
 */
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());
    private static AuthService instance;

    private final SecretKey signingKey;
    private final long expiryMs;
    private final int bcryptRounds;

    // In-memory revocation set — tokens added here are rejected even if valid
    // Cleared on restart; sufficient for session management in a desktop app
    private final Set<String> revokedTokens = new HashSet<>();

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private AuthService() {
        ConfigService cfg = ConfigService.getInstance();

        String secret = cfg.get("jwt.secret");
        if (secret == null || secret.isBlank() || secret.startsWith("REPLACE_")) {
            LOG.severe("jwt.secret not configured — auth will not function correctly");
            secret = "fallback-insecure-key-replace-this-immediately-32chars";
        }
        // jjwt requires minimum 256-bit (32 byte) key for HS256
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            LOG.warning("jwt.secret is shorter than 32 characters — padding");
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.signingKey  = Keys.hmacShaKeyFor(keyBytes);
        this.expiryMs    = cfg.getLong("jwt.expiry.ms", 28800000L); // 8 hours
        this.bcryptRounds = 12;
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // JWT — issue
    // -------------------------------------------------------------------------

    /**
     * Issues a signed JWT containing the user's identity and role claims.
     * Call this after validating credentials against the database.
     *
     * @param userId       user_id from USER or GUEST table
     * @param role         role string — use Claims.ROLE_* constants
     * @param orgId        org_id the user belongs to (null for guests)
     * @param enterpriseId enterprise_id (null for guests)
     * @param email        user's email address
     * @return signed JWT string
     */
    public String issueJWT(String userId, String role, String orgId,
                            String enterpriseId, String email) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
            .setSubject(userId)
            .claim("role",         role)
            .claim("orgId",        orgId != null ? orgId : "")
            .claim("enterpriseId", enterpriseId != null ? enterpriseId : "")
            .claim("email",        email != null ? email : "")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    // -------------------------------------------------------------------------
    // JWT — validate
    // -------------------------------------------------------------------------

    /**
     * Validates a JWT string and returns its Claims if valid and not revoked.
     * Returns null if the token is invalid, expired, or revoked.
     * Call this at every panel transition before rendering role-specific content.
     *
     * @param token JWT string from the current session
     * @return Claims object or null if invalid
     */
    public Claims validateJWT(String token) {
        if (token == null || token.isBlank()) return null;
        if (revokedTokens.contains(token))   return null;

        try {
            io.jsonwebtoken.Claims raw = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            return new Claims(
                raw.getSubject(),
                raw.get("role",         String.class),
                raw.get("orgId",        String.class),
                raw.get("enterpriseId", String.class),
                raw.get("email",        String.class),
                raw.getIssuedAt(),
                raw.getExpiration()
            );

        } catch (ExpiredJwtException e) {
            LOG.info("JWT expired for subject: " + e.getClaims().getSubject());
            return null;
        } catch (JwtException e) {
            LOG.warning("Invalid JWT: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convenience — returns true if the token is non-null, valid, and not expired.
     * Use this for quick gate checks before calling validateJWT for the full claims.
     */
    public boolean isTokenValid(String token) {
        return validateJWT(token) != null;
    }

    // -------------------------------------------------------------------------
    // JWT — revocation
    // -------------------------------------------------------------------------

    /**
     * Revokes a token immediately. Revoked tokens are rejected by validateJWT
     * even if they haven't expired. Call this on logout.
     */
    public void revokeToken(String token) {
        if (token != null) {
            revokedTokens.add(token);
            LOG.info("Token revoked");
        }
    }

    /** Clears all revoked tokens — call on application restart */
    public void clearRevocations() {
        revokedTokens.clear();
    }

    // -------------------------------------------------------------------------
    // BCrypt — password hashing
    // -------------------------------------------------------------------------

    /**
     * Hashes a plaintext password using BCrypt.
     * Store the returned hash in the database — never store the plaintext.
     *
     * @param plaintext raw password from the user
     * @return BCrypt hash string
     */
    public String hashPassword(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(bcryptRounds));
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     *
     * @param plaintext raw password from login form
     * @param hash      stored hash from database
     * @return true if the password matches
     */
    public boolean verifyPassword(String plaintext, String hash) {
        if (plaintext == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plaintext, hash);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "BCrypt verification error", e);
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Password policy
    // -------------------------------------------------------------------------

    /**
     * Enforces password policy:
     *   - Minimum 8 characters
     *   - At least one uppercase letter
     *   - At least one lowercase letter
     *   - At least one digit
     *   - At least one special character
     *
     * @param password plaintext password to check
     * @return true if password meets policy
     */
    public boolean enforcePasswordPolicy(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper   = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower   = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit   = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c ->
            "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0);
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Returns a human-readable description of why a password fails policy.
     * Use this to populate form validation error messages.
     */
    public String getPasswordPolicyMessage(String password) {
        if (password == null || password.length() < 8)
            return "Password must be at least 8 characters.";
        if (!password.chars().anyMatch(Character::isUpperCase))
            return "Password must contain at least one uppercase letter.";
        if (!password.chars().anyMatch(Character::isLowerCase))
            return "Password must contain at least one lowercase letter.";
        if (!password.chars().anyMatch(Character::isDigit))
            return "Password must contain at least one number.";
        if (!password.chars().anyMatch(c ->
                "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0))
            return "Password must contain at least one special character.";
        return null; // null = policy satisfied
    }
}