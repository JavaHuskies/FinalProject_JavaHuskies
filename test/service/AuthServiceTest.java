package service;

import model.Guest;
import model.GuestRegistration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AuthService.
 * Covers BCrypt hashing, JWT lifecycle, password policy enforcement,
 * and guest registration / email verification.
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
        String hash1 = auth.hashPassword("Admin1!");
        String hash2 = auth.hashPassword("Admin1!");
        assertNotEquals("Each hash should be unique due to random salt", hash1, hash2);
    }

    // ── JWT issue / validate / revoke ─────────────────────────────────────────

    /** Issues a token for the netadmin stub user — shared across JWT tests. */
    private String netadminToken() {
        return auth.issueJWT("netadmin", "networkAdmin",
                             "slartibartfastPictures", "magratheaStudios",
                             "netadmin@deepthought.local");
    }

    @Test
    public void testIssueJWTReturnsToken() {
        String token = netadminToken();
        assertNotNull("Token should not be null", token);
        assertFalse("Token should not be empty", token.isEmpty());
    }

    @Test
    public void testValidateJWTReturnsClaimsForValidToken() {
        model.Claims claims = auth.validateJWT(netadminToken());
        assertNotNull("Claims should not be null for valid token", claims);
    }

    @Test
    public void testValidateJWTClaimsContainCorrectRole() {
        model.Claims claims = auth.validateJWT(netadminToken());
        assertEquals("Role should match issued value",
                     "networkAdmin", claims.getRole());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectUserId() {
        model.Claims claims = auth.validateJWT(netadminToken());
        assertEquals("User ID should match issued value",
                     "netadmin", claims.getUserId());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectOrg() {
        model.Claims claims = auth.validateJWT(netadminToken());
        assertEquals("Org ID should match issued value",
                     "slartibartfastPictures", claims.getOrgId());
    }

    @Test
    public void testValidateJWTClaimsContainCorrectEnterprise() {
        model.Claims claims = auth.validateJWT(netadminToken());
        assertEquals("Enterprise ID should match issued value",
                     "magratheaStudios", claims.getEnterpriseId());
    }

    @Test
    public void testValidateJWTReturnNullForGarbageToken() {
        assertNull("Garbage token should return null claims",
                   auth.validateJWT("this.is.not.a.jwt"));
    }

    @Test
    public void testValidateJWTReturnNullForNullToken() {
        assertNull("Null token should return null claims",
                   auth.validateJWT(null));
    }

    @Test
    public void testRevokedTokenFailsValidation() {
        String token = netadminToken();
        auth.revokeToken(token);
        assertNull("Revoked token should not validate", auth.validateJWT(token));
    }

    // ── Password policy ───────────────────────────────────────────────────────

    @Test
    public void testPasswordPolicyPassesValidPassword() {
        assertTrue("Admin1! should pass policy",
                   auth.enforcePasswordPolicy("Admin1!"));
    }

    @Test
    public void testPasswordPolicyFailsNoUppercase() {
        assertFalse("admin1! has no uppercase - should fail",
                    auth.enforcePasswordPolicy("admin1!"));
    }

    @Test
    public void testPasswordPolicyFailsNoDigit() {
        assertFalse("AdminOne! has no digit - should fail",
                    auth.enforcePasswordPolicy("AdminOne!"));
    }

    @Test
    public void testPasswordPolicyFailsNoSpecialChar() {
        assertFalse("Admin123 has no special char - should fail",
                    auth.enforcePasswordPolicy("Admin123"));
    }

    @Test
    public void testPasswordPolicyFailsTooShort() {
        assertFalse("Ad1! is too short - should fail",
                    auth.enforcePasswordPolicy("Ad1!"));
    }

    @Test
    public void testPasswordPolicyFailsNull() {
        assertFalse("Null password should fail policy",
                    auth.enforcePasswordPolicy(null));
    }

    @Test
    public void testPasswordPolicyMessageReturnedForWeakPassword() {
        String msg = auth.getPasswordPolicyMessage("weak");
        assertNotNull("Policy message should not be null for weak password", msg);
        assertFalse("Policy message should not be empty", msg.isEmpty());
    }

    @Test
    public void testPasswordPolicyMessageNullForStrongPassword() {
        assertNull("Policy message should be null when password meets policy",
                   auth.getPasswordPolicyMessage("Admin1!"));
    }

    // ── registerGuest ─────────────────────────────────────────────────────────

    @Test
    public void testRegisterGuestReturnsGuest() {
        assertNotNull("registerGuest should return a Guest object",
                      auth.registerGuest(validForm()));
    }

    @Test
    public void testRegisterGuestEmailNotVerified() {
        assertFalse("New guest should have emailVerified = false",
                    auth.registerGuest(validForm()).isEmailVerified());
    }

    @Test
    public void testRegisterGuestPasswordIsHashed() {
        GuestRegistration form = validForm();
        Guest guest = auth.registerGuest(form);
        assertNotEquals("Password should be stored as hash, not plaintext",
                        form.getPassword(), guest.getPasswordHash());
        assertTrue("Stored hash should be a BCrypt hash",
                   guest.getPasswordHash().startsWith("$2"));
    }

    @Test
    public void testRegisterGuestEmailMatches() {
        assertEquals("Guest email should match form input",
                     "zaphod@heartofgold.com",
                     auth.registerGuest(validForm()).getEmail());
    }

    @Test
    public void testRegisterGuestVerificationTokenNotNull() {
        Guest guest = auth.registerGuest(validForm());
        assertNotNull("Verification token should not be null",
                      guest.getVerificationToken());
        assertFalse("Verification token should not be empty",
                    guest.getVerificationToken().isBlank());
    }

    @Test
    public void testRegisterGuestUniqueIdsPerRegistration() {
        Guest g1 = auth.registerGuest(validForm());
        Guest g2 = auth.registerGuest(validForm());
        assertNotEquals("Each registration should produce a unique guestId",
                        g1.getGuestId(), g2.getGuestId());
        assertNotEquals("Each registration should produce a unique verification token",
                        g1.getVerificationToken(), g2.getVerificationToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterGuestNullFormThrows() {
        auth.registerGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterGuestBlankEmailThrows() {
        auth.registerGuest(new GuestRegistration(
                "Zaphod", "Beeblebrox", "", "Admin1!", "Admin1!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterGuestPasswordMismatchThrows() {
        auth.registerGuest(new GuestRegistration(
                "Zaphod", "Beeblebrox", "zaphod@heartofgold.com",
                "Admin1!", "Different1!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterGuestWeakPasswordThrows() {
        auth.registerGuest(new GuestRegistration(
                "Zaphod", "Beeblebrox", "zaphod@heartofgold.com",
                "weak", "weak"));
    }

    // ── confirmVerification ───────────────────────────────────────────────────

    @Test
    public void testConfirmVerificationReturnsFalseWithoutPersistence() {
        // Stub behaviour — PersistenceService not wired yet.
        // Confirms the method compiles, runs, and returns false gracefully.
        assertFalse("confirmVerification should return false until PersistenceService is wired",
                    auth.confirmVerification("any-token"));
    }

    @Test
    public void testConfirmVerificationNullTokenReturnsFalse() {
        assertFalse("Null token should return false",
                    auth.confirmVerification(null));
    }

    @Test
    public void testConfirmVerificationBlankTokenReturnsFalse() {
        assertFalse("Blank token should return false",
                    auth.confirmVerification("   "));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns a valid registration form for use across guest tests. */
    private GuestRegistration validForm() {
        return new GuestRegistration(
                "Zaphod", "Beeblebrox",
                "zaphod@heartofgold.com",
                "Admin1!", "Admin1!");
    }
}