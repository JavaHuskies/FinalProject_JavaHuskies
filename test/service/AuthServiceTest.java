package service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AuthService.
 * Covers BCrypt hashing, JWT lifecycle, and password policy enforcement.
 *
 * @author John Baldwin
 */
public class AuthServiceTest {

    private AuthService auth;

    @Before
    public void setUp() {
        auth = AuthService.getInstance();
    }

    // ── hashPassword / verifyPassword ─────────────────────────────────────────

    @Test
    public void testHashPasswordReturnsNonNullHash() {
        String hash = auth.hashPassword("Admin1!");
        assertNotNull("Hash should not be null", hash);
        assertFalse("Hash should not be empty", hash.isEmpty());
    }

    @Test
    public void testHashPasswordProducesBcryptHash() {
        String hash = auth.hashPassword("Admin1!");
        assertTrue("Hash should start with BCrypt prefix", hash.startsWith("$2"));
    }

    @Test
    public void testVerifyPasswordCorrect() {
        String hash = auth.hashPassword("Admin1!");
        assertTrue("Correct password should verify", auth.verifyPassword("Admin1!", hash));
    }

    @Test
    public void testVerifyPasswordIncorrect() {
        String hash = auth.hashPassword("Admin1!");
        assertFalse("Wrong password should not verify", auth.verifyPassword("wrong", hash));
    }

    @Test
    public void testVerifyPasswordNullPassword() {
        String hash = auth.hashPassword("Admin1!");
        assertFalse("Null password should not verify", auth.verifyPassword(null, hash));
    }

    @Test
    public void testTwoHashesOfSamePasswordAreUnique() {
        // BCrypt generates unique salts each time
        String hash1 = auth.hashPassword("Admin1!");
        String hash2 = auth.hashPassword("Admin1!");
        assertNotEquals("Each hash should be unique due to random salt", hash1, hash2);
    }

    // ── JWT issue / validate / revoke ─────────────────────────────────────────

    @Test
    public void testIssueJWTReturnsToken() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        assertNotNull("Token should not be null", token);
        assertFalse("Token should not be empty", token.isEmpty());
    }

    @Test
    public void testValidateJWTReturnsClaimsForValidToken() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        model.Claims claims = auth.validateJWT(token);
        assertNotNull("Claims should not be null for valid token", claims);
    }

    @Test
    public void testValidateJWTClaimsContainCorrectRole() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        model.Claims claims = auth.validateJWT(token);
        assertEquals("Role should match issued value",
                     "networkAdmin", claims.getRole());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectUserId() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        model.Claims claims = auth.validateJWT(token);
        assertEquals("User ID should match issued value", "netadmin", claims.getUserId());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectOrg() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        model.Claims claims = auth.validateJWT(token);
        assertEquals("Org ID should match issued value",
                     "slartibartfastPictures", claims.getOrgId());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectEnterprise() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        model.Claims claims = auth.validateJWT(token);
        assertEquals("Enterprise ID should match issued value",
                     "magratheaStudios", claims.getEnterpriseId());
    }

    @Test
    public void testValidateJWTReturnNullForGarbageToken() {
        model.Claims claims = auth.validateJWT("this.is.not.a.jwt");
        assertNull("Garbage token should return null claims", claims);
    }

    @Test
    public void testValidateJWTReturnNullForNullToken() {
        model.Claims claims = auth.validateJWT(null);
        assertNull("Null token should return null claims", claims);
    }

    @Test
    public void testRevokedTokenFailsValidation() {
        String token = auth.issueJWT("netadmin", "networkAdmin",
                                     "slartibartfastPictures", "magratheaStudios");
        auth.revokeToken(token);
        model.Claims claims = auth.validateJWT(token);
        assertNull("Revoked token should not validate", claims);
    }

    // ── Password policy ───────────────────────────────────────────────────────

    @Test
    public void testPasswordPolicyPassesValidPassword() {
        assertTrue("Admin1! should pass policy",
                   auth.enforcePasswordPolicy("Admin1!"));
    }

    @Test
    public void testPasswordPolicyFailsNoUppercase() {
        assertFalse("admin1! has no uppercase — should fail",
                    auth.enforcePasswordPolicy("admin1!"));
    }

    @Test
    public void testPasswordPolicyFailsNoDigit() {
        assertFalse("AdminOne! has no digit — should fail",
                    auth.enforcePasswordPolicy("AdminOne!"));
    }

    @Test
    public void testPasswordPolicyFailsNoSpecialChar() {
        assertFalse("Admin123 has no special char — should fail",
                    auth.enforcePasswordPolicy("Admin123"));
    }

    @Test
    public void testPasswordPolicyFailsTooShort() {
        assertFalse("Ad1! is too short — should fail",
                    auth.enforcePasswordPolicy("Ad1!"));
    }

    @Test
    public void testPasswordPolicyFailsNull() {
        assertFalse("Null password should fail policy",
                    auth.enforcePasswordPolicy(null));
    }

    @Test
    public void testPasswordPolicyMessageIsNotEmpty() {
        String msg = auth.getPasswordPolicyMessage();
        assertNotNull("Policy message should not be null", msg);
        assertFalse("Policy message should not be empty", msg.isEmpty());
    }
}